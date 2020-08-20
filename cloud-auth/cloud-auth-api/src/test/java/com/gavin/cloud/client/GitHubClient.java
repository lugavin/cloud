package com.gavin.cloud.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign在默认情况下, 对于非2XX都认为是异常, 这是有问题的, 特别是对于404这种非常容易抛出的业务异常来说, 没两下就circuit break了.
 * Feign的Issue里已经有人提过这个问题, 后面的版本中已经提供了一个参数decode404.
 *
 * @see feign.SynchronousMethodHandler
 */
@FeignClient(name = "${api.github.name}", url = "${api.github.url}", decode404 = true, fallback = GitHubClientFallback.class)
public interface GitHubClient {

    @GetMapping("/users/{user}")
    String getUser(@PathVariable("user") String login);

}