package com.gavin.cloud.zookeeper.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * 基于Zookeeper实现的非公平锁
 * - 临时节点: 非公平锁
 * - 临时有序节点: 公平锁
 */
public class DistributedLock implements Lock {

    private static final String ROOT_PATH = "/locks";

    private static final String DEFAULT_CONNECTION_STRING = "127.0.0.1:2181";

    private static final int DEFAULT_SESSION_TIMEOUT = 2000;

    private static final int DEFAULT_CONNECTION_TIMEOUT = 2000;

    private final ZkClient zkClient;
    private final String lockName;

    public DistributedLock(String lockName) {
        this.lockName = ROOT_PATH + "/" + lockName;
        this.zkClient = new ZkClient(DEFAULT_CONNECTION_STRING, DEFAULT_SESSION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        if (!zkClient.exists(ROOT_PATH)) {
            // 如果根节点不存在则创建根节点
            zkClient.createPersistent(ROOT_PATH);
        }
    }

    @Override
    public void lock() {
        if (tryLock()) {
            System.err.println(Thread.currentThread().getName() + " >>> 成功获取到锁");
        } else {
            waitForLock();
            lock();
        }
    }

    @Override
    public boolean tryLock() {
        try {
            // 创建临时节点
            zkClient.createEphemeral(lockName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void unlock() {
        Optional.ofNullable(zkClient).ifPresent(zk -> {
            zkClient.close();
            System.err.println(Thread.currentThread().getName() + " >>> 释放锁");
        });
    }

    protected void waitForLock() {
        if (zkClient.exists(lockName)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            // 事件监听
            IZkDataListener dataListener = new DataDeletedListener(countDownLatch);
            // 订阅事件通知
            zkClient.subscribeDataChanges(lockName, dataListener);
            try {
                countDownLatch.await();
            } catch (Exception ignored) {
            }
            // 取消事件订阅
            zkClient.unsubscribeDataChanges(lockName, dataListener);
        }
    }

    private static class DataDeletedListener implements IZkDataListener {

        private final CountDownLatch countDownLatch;

        private DataDeletedListener(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void handleDataChange(String dataPath, Object data) throws Exception {
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
            Optional.ofNullable(countDownLatch).ifPresent(CountDownLatch::countDown);
        }

    }

}