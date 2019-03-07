package com.gavin.cloud.auth.client;

import com.gavin.cloud.sys.api.RoleApi;
import com.gavin.cloud.sys.api.SysApi;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(SysApi.SERVICE_NAME)
public interface RoleClient extends RoleApi {
}
