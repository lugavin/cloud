package com.gavin.cloud.sys.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static com.gavin.cloud.sys.api.SysApi.SERVICE_NAME;

@FeignClient(SERVICE_NAME)
public interface RoleApi extends SysApi {

    @GetMapping("/roles/{uid}/codes")
    List<String> getRoles(@PathVariable("uid") Long uid);

}
