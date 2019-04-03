package com.gavin.cloud.core.service;

import com.gavin.cloud.core.repository.UserRepository;
import com.gavin.cloud.pojo.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    public Mono<Void> deleteUser(String uid) {
        return userRepository.findById(uid).flatMap(userRepository::delete);
    }

    public Mono<User> updateUser(String uid, User user) {
        return userRepository.findById(uid).flatMap(existUser -> {
            // Set property that need to be updated
            existUser.setLogin(user.getLogin());
            existUser.setEmail(user.getEmail());
            existUser.setPhone(user.getPhone());
            existUser.setUpdatedAt(new Date());
            return userRepository.save(existUser);
        });
    }

    public Mono<User> getUserById(String uid) {
        return userRepository.findById(uid);
    }

    public Mono<User> getUserByLogin(String login) {
        return userRepository.findOneByLogin(login);
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

}
