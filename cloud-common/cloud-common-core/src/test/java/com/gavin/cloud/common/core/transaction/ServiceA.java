package com.gavin.cloud.common.core.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ServiceA {

    private final ServiceB serviceB;

    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }

    @Transactional
    public void execute() {
        doSomething();
        serviceB.execute();
        doSomethingElse();
    }

    private void doSomething() {
        log.debug("====== Invoke serviceA.doSomething() ======");
    }

    private void doSomethingElse() {
        log.debug("====== Invoke serviceA.doSomethingElse() ======");
    }

}
