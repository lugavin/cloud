package com.gavin.cloud.web;

import com.gavin.cloud.core.service.UserService;
import com.gavin.cloud.pojo.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Mono<User> createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{uid}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String uid) {
        return userService.deleteUser(uid)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @PutMapping("/{uid}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable String uid,
                                                 @Valid @RequestBody User user) {
        return userService.updateUser(uid, user).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{login}")
    public Mono<ResponseEntity<User>> getUserByLogin(@PathVariable("login") String login) {
        return userService.getUserByLogin(login)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
