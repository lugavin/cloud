package com.gavin.cloud.sys.api;

import com.gavin.cloud.sys.pojo.dto.RegisterDTO;
import com.gavin.cloud.sys.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static com.gavin.cloud.sys.api.SysApi.SERVICE_NAME;

@FeignClient(SERVICE_NAME)
public interface AccountApi extends SysApi {

    @PostMapping("/account/register")
    User register(@RequestBody RegisterDTO registerDTO);

    @GetMapping("/account/activate")
    User activateRegistration(@RequestParam("key") String key);

    @GetMapping("/account/reset-password/init")
    User requestPasswordReset(@RequestParam("mail") String mail);

    @PostMapping("/account/reset-password/finish/{key}")
    User finishPasswordReset(@PathVariable("key") String key, @RequestBody String newPassword);

}
