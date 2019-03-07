package com.gavin.cloud.sys.core.service;

import com.gavin.cloud.sys.api.dto.RegisterDTO;
import com.gavin.cloud.sys.api.model.User;

public interface AccountService {

    User register(RegisterDTO registerDTO);

    User activateRegistration(String key);

    User requestPasswordReset(String mail);

    User finishPasswordReset(String key, String newPassword);

    void changePassword(String id, String password);

}
