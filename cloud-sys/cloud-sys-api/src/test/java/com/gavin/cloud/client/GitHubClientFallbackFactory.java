package com.gavin.cloud.client;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Feign在默认情况下, 对于非2XX都认为是异常, 这是有问题的, 特别是对于404这种非常容易抛出的业务异常来说, 没两下就circuit break了.
 * Feign的Issue里已经有人提过这个问题, 后面的版本中已经提供了一个参数decode404.
 *
 * @see feign.SynchronousMethodHandler
 */
@Slf4j
@Component
public class GitHubClientFallbackFactory implements FallbackFactory<GitHubClient> {

    @Override
    public GitHubClient create(Throwable cause) {
        log.warn("fallback due to: " + cause.getMessage(), cause);
        return u -> HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
    }

}
