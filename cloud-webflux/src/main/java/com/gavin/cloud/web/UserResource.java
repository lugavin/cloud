package com.gavin.cloud.web;

import com.gavin.cloud.core.service.UserService;
import com.gavin.cloud.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{login}")
    public Mono<User> getById(@PathVariable("login") String login) {
        return userService.getByLogin(login);
    }

}
