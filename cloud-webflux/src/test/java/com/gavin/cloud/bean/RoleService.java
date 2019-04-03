package com.gavin.cloud.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class RoleService {

    @Autowired
    private UserService userService;

    public RoleService() {
        Optional.ofNullable(userService)
                .ifPresent(r -> log.info("userService is null"));
    }

    @PostConstruct
    public void afterPropertiesSet() {
        Objects.requireNonNull(userService);

    }

}
