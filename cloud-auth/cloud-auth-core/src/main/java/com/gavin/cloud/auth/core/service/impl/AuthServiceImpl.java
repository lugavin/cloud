package com.gavin.cloud.auth.core.service.impl;

import com.gavin.cloud.auth.core.config.properties.JwtExtProperties;
import com.gavin.cloud.auth.core.dto.AuthTokenDTO;
import com.gavin.cloud.auth.core.mapper.ext.AuthTokenMapperExt;
import com.gavin.cloud.auth.core.service.AuthService;
import com.gavin.cloud.auth.pojo.AuthToken;
import com.gavin.cloud.auth.pojo.AuthTokenExample;
import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.JwtHelper;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthTokenMapperExt authTokenMapperExt;
    private final JwtExtProperties jwtProperties;

    public AuthServiceImpl(AuthTokenMapperExt authTokenMapperExt, JwtExtProperties jwtProperties) {
        this.authTokenMapperExt = authTokenMapperExt;
        this.jwtProperties = jwtProperties;
    }

    @Override
    @Transactional
    public AuthTokenDTO createAuthToken(ActiveUser activeUser) {
        String accessToken = JwtHelper.createToken(activeUser, jwtProperties.getPrivateKey(), jwtProperties.getAccessTokenExpires());
        String refreshToken = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken();
        authToken.setId(SnowflakeIdWorker.getInstance().nextId());
        authToken.setUid(activeUser.getUid());
        authToken.setClientIp(activeUser.getClientIP());
        authToken.setRefreshToken(refreshToken);
        authToken.setCreatedAt(Date.from(Instant.now()));
        authToken.setExpiredAt(Date.from(Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpires())));
        authTokenMapperExt.insert(authToken);
        return new AuthTokenDTO(accessToken, refreshToken, jwtProperties.getAccessTokenExpires());
    }

    @Override
    @Transactional
    public AuthTokenDTO createAuthToken(ActiveUser activeUser, String refreshToken) {
        AuthTokenExample example = new AuthTokenExample();
        AuthTokenExample.Criteria criteria = example.createCriteria();
        criteria.andRefreshTokenEqualTo(refreshToken);
        List<AuthToken> authTokens = authTokenMapperExt.selectByExample(example);
        if (!authTokens.isEmpty()) {
            AuthToken authToken = authTokens.get(0);
            if (authToken.getId().equals(activeUser.getUid()) && authToken.getExpiredAt().after(Date.from(Instant.now()))) {
                String accessToken = JwtHelper.createToken(activeUser, jwtProperties.getPrivateKey(), jwtProperties.getAccessTokenExpires());
                return new AuthTokenDTO(accessToken, refreshToken, jwtProperties.getAccessTokenExpires());
            }
        }
        throw new RuntimeException("The refresh token has expired!");
    }

    @Override
    public ActiveUser verifyAccessToken(String accessToken) {
        return JwtHelper.verifyToken(accessToken, jwtProperties.getPublicKey());
    }

    @Override
    @Transactional
    public void rejectRefreshToken(String refreshToken) {
        AuthTokenExample example = new AuthTokenExample();
        AuthTokenExample.Criteria criteria = example.createCriteria();
        criteria.andRefreshTokenEqualTo(refreshToken);
        authTokenMapperExt.deleteByExample(example);
    }

}
