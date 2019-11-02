package com.gavin.cloud.distributed;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.util.ResourceUtils;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedissonTest {


    private static final String COUNTER_KEY = "_COUNTER_";
    private static final long COUNTER_TIMES = 5;
    private static final long EXPIRE_SECONDS = 1800;

    private RedissonClient redisson;
    private CountDownRunner countDownRunner;

    @Before
    public void setUp() throws Exception {
        URL url = ResourceUtils.getURL("classpath:redisson.yml");
        redisson = Redisson.create(Config.fromYAML(url));
        countDownRunner = new CountDownRunner(((Redisson) redisson).getCommandExecutor());
    }

    @Test
    public void testInitCounter() {
        countDownRunner.initCounter(COUNTER_KEY, COUNTER_TIMES, EXPIRE_SECONDS);
    }

    @Test
    public void testCountDown() {
        countDownRunner.countDown(COUNTER_KEY, () -> log.debug("计数器递减为零执行业务处理"));
    }

    @Test
    public void testLock() throws Exception {
        String lockName = "METHOD_LOCK";
        RLock lock = redisson.getLock(lockName);
        if (lock.tryLock()) {
            try {
                log.debug("====== 获取到锁, 执行业务操作 ======");
                TimeUnit.SECONDS.sleep(1);
            } finally {
                lock.unlock();
            }
        }
    }

}
