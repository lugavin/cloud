package com.gavin.cloud.distributed.redisson;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedissonTest {

    public static void main(String[] args) throws Exception {
        URL url = Thread.currentThread().getContextClassLoader().getResource("redisson.yml");
        Config config = Config.fromYAML(url);
        RedissonClient redisson = Redisson.create(config);
        RLock lock = redisson.getLock("LOCK");
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
