package com.gavin.cloud.core.service;

import com.gavin.cloud.pojo.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private Map<String, User> users = Collections.singletonMap("admin", new User("admin", "admin@gmail.com"));

    public Mono<User> getByLogin(String login) {
        return Mono.justOrEmpty(users.get(login))
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

}
