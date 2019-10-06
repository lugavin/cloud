package com.gavin.cloud.distributed;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
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

    private RedissonClient redisson;

    @Before
    public void setUp() throws Exception {
        //URL url = Thread.currentThread().getContextClassLoader().getResource("redisson.yml");
        URL url = ResourceUtils.getURL("classpath:redisson.yml");
        Config config = Config.fromYAML(url);
        redisson = Redisson.create(config);
    }

    @After
    public void tearDown() {
        //if (redisson != null) {
        //    redisson.shutdown();
        //}
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
        log.debug("====== {} ======", redisson.getAtomicLong("ORDER_NO").incrementAndGet());
    }

}
