package com.gavin.cloud.common.base.builder;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

@Slf4j
public class BuilderTest {

    @Test
    public void testBuild() throws Exception {
        MethodLock methodLock = MethodLock.builder()
                .id(UUID.randomUUID().toString())
                .methodName("CreateUser")
                .updatedAt(new Date())
                .build();
        log.debug("====== {} ======", methodLock);
    }

}
