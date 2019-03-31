package com.gavin.cloud.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${api.github.name}", url = "${api.github.url}", decode404 = true, fallback = GitHubClientFallback.class)
public interface GitHubClient {

    @GetMapping("/users/{user}")
    String getUser(@PathVariable("user") String user);

}