package com.gavin.cloud.distributed;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorTest {

    public static void main(String[] args) throws Exception {
        String lockName = "/locks/METHOD_LOCK";
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();
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
