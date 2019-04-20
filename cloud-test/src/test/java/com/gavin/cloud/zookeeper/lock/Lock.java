package com.gavin.cloud.zookeeper.lock;

public interface Lock {

    void lock();

    boolean tryLock();

    void unlock();

}