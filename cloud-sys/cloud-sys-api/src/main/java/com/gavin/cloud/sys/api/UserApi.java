package com.gavin.cloud.sys.api;

import com.gavin.cloud.sys.api.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserApi {

    @PostMapping("/users")
    User createUser(@RequestBody User user);

    @GetMapping("/users/{id}")
    User getUser(@PathVariable("id") String id);

    @GetMapping("/users/{account}/{type}")
    User getUser(@PathVariable("account") String account, @PathVariable("type") int type);

}
