package com.gavin.cloud.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private RoleService roleService;

    public UserService() {
        Optional.ofNullable(roleService)
                .ifPresent(r -> log.info("roleService is null"));
    }

    @PostConstruct
    public void afterPropertiesSet() {
        Objects.requireNonNull(roleService);
    }

}
