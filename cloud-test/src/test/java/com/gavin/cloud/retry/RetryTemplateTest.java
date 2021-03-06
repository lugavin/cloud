package com.gavin.cloud.retry;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.retry.policy.SimpleRetryPolicy.DEFAULT_MAX_ATTEMPTS;

/**
 * @author gavin.lu
 * @since 2.0.0
 */
@Slf4j
public class RetryTemplateTest {

    private RetryTemplate retryTemplate;

    private AtomicInteger counter = new AtomicInteger();

    @BeforeEach
    public void setUp() {
        retryTemplate = new RetryTemplate();
        // 固定次数重试策略: 默认重试最大次数为3次
        RetryPolicy retryPolicy = new SimpleRetryPolicy(DEFAULT_MAX_ATTEMPTS, Collections.singletonMap(RetryException.class, Boolean.TRUE));
        retryTemplate.setRetryPolicy(retryPolicy);
    }

    @Test
    public void testRetry() {
        retryTemplate.execute(c -> {
            if (counter.incrementAndGet() < DEFAULT_MAX_ATTEMPTS) {
                throw new RetryException("模拟业务处理异常");
            }
            log.info("业务第 {} 次处理成功", counter.get());
            return null;
        });
    }

    private static class RetryException extends RuntimeException {
        RetryException(String message) {
            super(message);
        }
    }

}
