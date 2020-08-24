package com.gavin.cloud.sys.api;

import com.gavin.cloud.sys.pojo.Permission;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

import static com.gavin.cloud.sys.api.SysApi.SERVICE_NAME;

@FeignClient(SERVICE_NAME)
public interface PermissionApi extends SysApi {

    @GetMapping("/perms/codes/{roles}")
    Set<String> getPermCodes(@PathVariable("roles") String[] roles);

    @GetMapping("/perms/roles/{roles}")
    List<Permission> getPerms(@PathVariable("roles") String[] roles);

    @GetMapping("/perms/menus/{userId}")
    List<Permission> getMenus(@PathVariable("userId") Long userId);

    @GetMapping("/perms/funcs/{userId}")
    List<Permission> getFuncs(@PathVariable("userId") Long userId);

}
