package com.gavin.cloud.common.web.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient("cloud-sys")
public interface SysApi {

    @GetMapping("/perms/codes/{roles}")
    Set<String> getPermissionCodes(@PathVariable("roles") String[] roles);

}
