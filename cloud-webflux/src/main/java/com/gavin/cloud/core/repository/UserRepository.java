package com.gavin.cloud.core.repository;

import com.gavin.cloud.pojo.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findOneByLogin(String login);

}
