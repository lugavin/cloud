package com.gavin.cloud;

import com.gavin.cloud.retry.RemoteService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.EnableRetry;

import java.util.concurrent.TimeUnit;

@Slf4j
@EnableRetry
@SpringBootApplication
public class CloudApp {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(CloudApp.class, args);
        RedissonClient redisson = context.getBean(RedissonClient.class);
        RLock lock = redisson.getLock("LOCK");
        if (lock.tryLock()) {
            try {
                log.debug("====== 获取到锁, 执行业务操作 ======");
                TimeUnit.SECONDS.sleep(1);
            } finally {
                lock.unlock();
            }
        }
        RemoteService remoteService = context.getBean(RemoteService.class);
        remoteService.call();
    }

}
