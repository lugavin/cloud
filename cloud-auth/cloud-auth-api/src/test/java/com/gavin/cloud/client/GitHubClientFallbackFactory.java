package com.gavin.cloud.client;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GitHubClientFallbackFactory implements FallbackFactory<GitHubClient> {

    @Override
    public GitHubClient create(Throwable cause) {
        log.warn("fallback due to: " + cause.getMessage(), cause);
        return u -> HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
    }

}
