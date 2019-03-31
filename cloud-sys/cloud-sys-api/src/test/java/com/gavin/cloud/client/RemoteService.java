package com.gavin.cloud.client;

import com.gavin.cloud.config.properties.GitHubProperties;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@DefaultProperties(defaultFallback = "defaultFallback")
public class RemoteService {

    private final RestTemplate restTemplate;
    private final GitHubProperties gitHubProperties;

    public RemoteService(RestTemplate restTemplate, GitHubProperties gitHubProperties) {
        this.restTemplate = restTemplate;
        this.gitHubProperties = gitHubProperties;
    }

    @HystrixCommand
    public String getUser(@NonNull String login) {
        return restTemplate.getForObject(gitHubProperties.getUrl() + "/users/" + login, String.class);
    }

    @HystrixCommand(fallbackMethod = "getUserInfoFallback")
    public String getUserInfo(@NonNull String login) {
        return restTemplate.getForObject(gitHubProperties.getUrl() + "/users/" + login, String.class);
    }

    private String getUserInfoFallback(String login) {
        return HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
    }

    private String defaultFallback() {
        return HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
    }

}
