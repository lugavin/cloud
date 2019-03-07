package com.gavin.cloud.sys.api;

import com.gavin.cloud.sys.api.model.Permission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface PermissionApi {

    @GetMapping(value = "/permissions/{userId}/menus")
    List<Permission> getMenus(@PathVariable("userId") String userId);

    @GetMapping(value = "/permissions/{userId}/funcs")
    List<Permission> getFuncs(@PathVariable("userId") String userId);

}
