package com.gavin.cloud.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.*;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.stereotype.Component;

/**
 * 被{@link Retryable}注解的方法发生异常时会重试
 * - include: 指定发生的异常进行重试
 * - maxAttemps: 重试次数
 * - backoff: 重试补偿机制
 * <p>
 * {@link Backoff}
 * - delay: 指定延迟后重试
 * - multiplier: 指定延迟的倍数, 比如delay=5000L&multiplier=2时, 第一次重试为5秒后, 第二次为10秒, 第三次为20秒
 * <p>
 * {@link Recover}: 当重试到达指定次数时, 被注解的方法将被回调, 可以在该方法中进行日志处理, 需要注意的是发生的异常和入参类型一致时才会回调
 * <p>
 * 注意: 通常重试对GET请求有效, 对其他请求方式开启重试业务必须实现幂等.
 */
@Slf4j
@Component
@EnableRetry
public class RetryService {

    /**
     * spring-retry 默认的重试策略为 {@link SimpleRetryPolicy}
     */
    @Retryable(include = {RemoteAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 5000L, multiplier = 1))
    public void call() {
        log.info("====== Do something ======");
        throw new RemoteAccessException("RPC调用异常");
    }

    /**
     * {@link RecoverAnnotationRecoveryHandler}
     */
    @Recover
    private void recover(RemoteAccessException e) {
        log.error(e.getMessage());
    }

}