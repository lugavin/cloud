package com.gavin.cloud.sys.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface RoleApi {

    @GetMapping("/roles/{userId}/codes")
    List<String> getRoles(@PathVariable("userId") String userId);

}
