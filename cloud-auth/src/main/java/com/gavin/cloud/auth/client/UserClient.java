package com.gavin.cloud.auth.client;

import com.gavin.cloud.sys.api.UserApi;
import org.springframework.cloud.netflix.feign.FeignClient;

import static com.gavin.cloud.sys.api.SysApi.SERVICE_NAME;

@FeignClient(SERVICE_NAME)
///@DefaultProperties(defaultFallback = "")
public interface UserClient extends UserApi {

    //@HystrixCommand(fallbackMethod = "createUserFallback")
    //default User createUserFallback(User user) {
    //    throw new RuntimeException("服务器正忙");
    //}

}
