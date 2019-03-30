package com.gavin.cloud.auth.client;

import com.gavin.cloud.sys.api.AccountApi;
import org.springframework.cloud.netflix.feign.FeignClient;

import static com.gavin.cloud.sys.api.SysApi.SERVICE_NAME;

@FeignClient(SERVICE_NAME)
public interface AccountClient extends AccountApi {
}
