package com.gavin.cloud.resource;

import com.gavin.cloud.model.User;
import com.gavin.cloud.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@RestController
public class UserResource {

    private final UserRepository userRepository;

    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/users")
    public Mono<User> createUser(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    @DeleteMapping("/users/{uid}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String uid) {
        return userRepository.findById(uid)
                .flatMap(existUser ->
                        userRepository.delete(existUser)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{uid}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable String uid,
                                                 @Valid @RequestBody User user) {
        return userRepository.findById(uid).flatMap(existUser -> {
            existUser.setLogin(user.getLogin());
            existUser.setEmail(user.getEmail());
            existUser.setPhone(user.getPhone());
            existUser.setUpdatedAt(new Date());
            return userRepository.save(existUser);
        }).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{login}")
    public Mono<ResponseEntity<User>> getUserByLogin(@PathVariable String login) {
        return userRepository.findOneByLogin(login)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
