package com.gavin.cloud.sys.api;

import com.gavin.cloud.sys.pojo.Permission;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static com.gavin.cloud.sys.api.SysApi.SERVICE_NAME;

@FeignClient(SERVICE_NAME)
public interface PermissionApi extends SysApi {

    @GetMapping(value = "/permissions/{userId}/menus")
    List<Permission> getMenus(@PathVariable("userId") Long userId);

    @GetMapping(value = "/permissions/{userId}/funcs")
    List<Permission> getFuncs(@PathVariable("userId") Long userId);

}
