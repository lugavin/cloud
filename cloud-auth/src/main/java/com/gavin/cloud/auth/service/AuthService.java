package com.gavin.cloud.auth.service;

import com.gavin.cloud.auth.enums.AuthMessageType;
import com.gavin.cloud.auth.properties.AuthProperties;
import com.gavin.cloud.common.base.dto.ActiveUser;
import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.common.base.jwt.Jwt;
import com.gavin.cloud.common.base.jwt.JwtVerifier;
import com.gavin.cloud.common.base.jwt.Payload;
import com.gavin.cloud.common.web.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.gavin.cloud.common.base.jwt.cipher.Algorithm.Type.RS256;

@Service
public class AuthService {

    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private JwtProperties jwtProperties;

    public String createToken(ActiveUser activeUser) {
        long currTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return Jwt.builder()
                .withIss("https://auth.gavin.com")
                .withSub(activeUser.getUid())
                .withIat(Long.toString(currTimeSeconds))
                .withExp(Long.toString(currTimeSeconds + jwtProperties.getValidityInSeconds()))
                .with("ip", activeUser.getClientIP())
                .with("username", activeUser.getUsername())
                .sign(authProperties.getPrivateKey(), RS256);
    }

    public ActiveUser verifyToken(String token) {
        JwtVerifier verifier = Jwt.verifier(token);
        if (!verifier.verify(jwtProperties.getPublicKey())) {
            throw new AppException(AuthMessageType.ERR_UNAUTHORIZED);
        }
        Payload payload = verifier.getPayload();
        Map<String, String> map = payload.getMap();
        return new ActiveUser(payload.getSub(), map.get("ip"), map.get("username"));
    }

}
