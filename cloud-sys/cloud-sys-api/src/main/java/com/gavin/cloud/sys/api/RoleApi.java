package com.gavin.cloud.sys.api;

import com.gavin.cloud.sys.pojo.Role;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static com.gavin.cloud.sys.api.SysApi.SERVICE_NAME;

@FeignClient(SERVICE_NAME)
public interface RoleApi extends SysApi {

    @GetMapping("/roles/{uid}")
    List<Role> getRoles(@PathVariable("uid") Long uid);

}
