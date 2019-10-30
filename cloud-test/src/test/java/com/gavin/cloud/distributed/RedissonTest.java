package com.gavin.cloud.distributed;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.util.ResourceUtils;

import java.net.URL;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.redisson.api.RScript.Mode.READ_WRITE;
import static org.redisson.api.RScript.ReturnType.BOOLEAN;

@Slf4j
public class RedissonTest {

    private static final String LUA_SCRIPT =
            "local currValue = redis.call('get', KEYS[1]); "
                    + "if tonumber(ARGV[1]) == 0 and currValue == false then "
                    + "redis.call('set', KEYS[1], ARGV[2]); "
                    + "return 1 "
                    + "else "
                    + "return 0 "
                    + "end";

    private RedissonClient redisson;

    @Before
    public void setUp() throws Exception {
        URL url = ResourceUtils.getURL("classpath:redisson.yml");
        redisson = Redisson.create(Config.fromYAML(url));
    }


    @Test
    public void testScript() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        String key = "_COUNTER_";
        RScript script = redisson.getScript(StringCodec.INSTANCE);
        RFuture<Boolean> future = script.evalAsync(READ_WRITE, LUA_SCRIPT, BOOLEAN, Collections.singletonList(key), 0, 10);
        future.thenAccept(ret -> {
            if (ret) {
                redisson.getAtomicLong(key).expire(3600, TimeUnit.SECONDS);
                log.debug("计数器初始化成功");
            } else {
                log.debug("计数器已初始化");
            }
            latch.countDown();
        });
        latch.await();
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

    @Test
    public void testAtomic() {
        RAtomicLong rAtomicLong = redisson.getAtomicLong("ORDER_NO");
        log.debug("====== {} ======", rAtomicLong.incrementAndGet());
    }

}
