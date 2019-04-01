package com.gavin.cloud.distributed.lock;

public interface Lock {

    void lock();

    boolean tryLock();

    void unlock();

}
