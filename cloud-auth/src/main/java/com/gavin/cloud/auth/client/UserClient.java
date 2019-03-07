package com.gavin.cloud.auth.client;

import com.gavin.cloud.sys.api.SysApi;
import com.gavin.cloud.sys.api.UserApi;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(SysApi.SERVICE_NAME)
public interface UserClient extends UserApi {
}
