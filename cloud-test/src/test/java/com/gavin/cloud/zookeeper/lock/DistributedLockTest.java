package com.gavin.cloud.zookeeper.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * https://www.ibm.com/developerworks/cn/java/j-java8idioms3/index.html
 */
public class DistributedLockTest {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    private static int tickets = 10;

    public static void main(String[] args) {
        IntStream.iterate(tickets, i -> i--).limit(tickets).forEach(i -> threadPool.execute(() -> {
            Lock lock = new DistributedLock("METHOD_LOCK");
            lock.lock();
            try {
                System.err.println(String.format("%s >> 已抢到 %02d 号票", Thread.currentThread().getName(), tickets--));
            } finally {
                lock.unlock();
            }
        }));
        threadPool.shutdown();
    }

}