package com.gavin.cloud.distributed;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorTest {

    private CuratorFramework client;

    @Before
    public void setUp() {
        client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
    }

    @After
    public void tearDown() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void tearLock() throws Exception {
        String lockName = "/locks/METHOD_LOCK";
        InterProcessMutex lock = new InterProcessMutex(client, lockName);
        try {
            lock.acquire();
            log.debug("====== 获取到锁, 执行业务操作 ======");
            TimeUnit.SECONDS.sleep(1);
        } finally {
            lock.release();
        }
    }

}
