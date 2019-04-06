package com.gavin.cloud.sys.core.service;

import com.gavin.cloud.sys.pojo.User;
import com.gavin.cloud.sys.pojo.dto.RegisterDTO;

public interface AccountService {

    User register(RegisterDTO registerDTO);

    User activateRegistration(String key);

    User requestPasswordReset(String mail);

    User finishPasswordReset(String key, String newPassword);

    void changePassword(Long id, String password);

}
