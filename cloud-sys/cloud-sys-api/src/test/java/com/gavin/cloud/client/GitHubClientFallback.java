package com.gavin.cloud.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class GitHubClientFallback implements GitHubClient {

    @Override
    public String getUser(String login) {
        return HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
    }

}