package com.gavin.cloud.auth.core.service;

import com.gavin.cloud.auth.core.dto.AuthTokenDTO;
import com.gavin.cloud.common.base.auth.ActiveUser;

public interface AuthService {

    AuthTokenDTO createAuthToken(ActiveUser activeUser);

    AuthTokenDTO createAuthToken(ActiveUser activeUser, String refreshToken);

    ActiveUser verifyAccessToken(String accessToken);

    void rejectRefreshToken(String refreshToken);

}
