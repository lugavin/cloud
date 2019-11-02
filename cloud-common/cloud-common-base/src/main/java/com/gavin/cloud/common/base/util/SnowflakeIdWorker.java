package com.gavin.cloud.common.base.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Twitter的Snowflake算法(用于分布式自增长ID), 其原理结构如下(每部分用-分开):
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * (1)1位标识, 由于long基本类型在Java中是带符号的, 最高位是符号位, 正数是0, 负数是1, 因为id一般是正数, 最高位是0;
 * (2)41位时间戳(毫秒级), 注意, 41位时间戳不是存储当前时间的时间戳, 而是存储当前时间戳与开始时间戳的差值, 这里的的开始时间戳, 一般是我们的id生成器开始使用的时间, 由我们程序来指定的(41位的时间戳可以使用2^41/1000*3600*24*365=69年);
 * (3)10位的数据机器位, 可以部署在1024个节点, 包括5位datacenterId和5位workerId;
 * (4)12位序列, 毫秒内的计数, 12位的计数顺序号支持每个节点每毫秒(同一机器同一时间戳)产生4096个ID序号;
 * Snowflake的优点是: 整体上按照时间自增排序, 并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分), 并且效率较高, 经测试, SnowFlake每秒能够产生26万ID左右.
 *
 * @see <a href="https://github.com/twitter/snowflake">Snowflake</a>
 */
@Slf4j
public class SnowflakeIdWorker {

    // 开始时间截, 一旦确定不能变动
    private static final long twepoch = LocalDate.parse("2015-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

    // 机器id所占的位数
    private static final long workerIdBits = 5L;

    // 数据标识id所占的位数
    private static final long datacenterIdBits = 5L;

    // 支持的最大机器id, 结果是31
    private static final long maxWorkerId = ~(-1L << workerIdBits);

    // 支持的最大数据标识id, 结果是31
    private static final long maxDatacenterId = ~(-1L << datacenterIdBits);

    // 序列在id中占的位数
    private static final long sequenceBits = 12L;

    // 机器id向左移12位
    private static final long workerIdShift = sequenceBits;

    // 数据标识id向左移17位(12+5)
    private static final long datacenterIdShift = sequenceBits + workerIdBits;

    // 时间截向左移22位(5+5+12)
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 生成序列的掩码, 这里为4095(0b111111111111=0xfff=4095)
    private static final long sequenceMask = ~(-1L << sequenceBits);

    // 上次生成id的时间截
    private static long lastTimestamp = -1L;

    // 毫秒内序列(0~4095)
    private long sequence = 0L;

    // 工作机器id(0~31)
    private final long workerId;

    // 数据中心id(0~31)
    private final long datacenterId;

    private SnowflakeIdWorker() {
        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);
    }

    /**
     * @param workerId     工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    private SnowflakeIdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获得下一个ID(该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        // 如果当前时间小于上一次ID生成的时间戳, 说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        // 如果是同一时间生成的, 则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            // 当前毫秒内则+1
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 当前毫秒内计数满了则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变, 毫秒内序列重置
            sequence = 0L;
        }
        // 上次生成ID的时间截
        lastTimestamp = timestamp;
        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (datacenterId << datacenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒, 直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    private static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 1])
                        | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                id = id % (maxDatacenterId + 1);
            }
        } catch (IOException e) {
            log.error("[ SnowflakeIdWorker ] getDatacenterId exception", e);
        }
        return id;
    }

    private static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            mpid.append(name.split("@")[0]);
        }
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    public static SnowflakeIdWorker getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final SnowflakeIdWorker INSTANCE = new SnowflakeIdWorker();
    }

    //public static void main(String[] args) {
    //    Set<Long> ids = new HashSet<>();
    //    // 多个线程使用同一个对象(单例)
    //    //SnowflakeIdWorker idWorker = new SnowflakeIdWorker();
    //    IntStream.range(0, 1000).forEach(i -> CompletableFuture.runAsync(() -> {
    //        // 每个线程都新建一个对象, 那么每个线程的取时间戳可以同时进行, 序列自增也是, 所以才会产生相同的id
    //        SnowflakeIdWorker idWorker = new SnowflakeIdWorker();
    //        Long id = idWorker.nextId();
    //        if (!ids.add(id)) {
    //            System.err.println("存在重复ID >> " + id);
    //        } else {
    //            System.out.println("ID >> " + id);
    //        }
    //    }));
    //}

}