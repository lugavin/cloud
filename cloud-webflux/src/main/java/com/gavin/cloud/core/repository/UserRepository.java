package com.gavin.cloud.core.repository;

import com.gavin.cloud.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository /*extends JpaRepository<User, Long> */ {

    Optional<User> findOneByLogin(String login);

}
