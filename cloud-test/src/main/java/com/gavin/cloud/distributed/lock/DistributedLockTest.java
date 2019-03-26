package com.gavin.cloud.distributed.lock;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DistributedLockTest {

    private static int start = 0;

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                Lock lock = new DistributedLock("MY_LOCK");
                lock.lock();
                try {
                    String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + String.format("%06d", ++start);
                    System.err.printf("%s >>> 创建订单 [%s]", Thread.currentThread().getName(), orderNo).println();
                } finally {
                    lock.unlock();
                }
            }).start();
        }
    }

}
