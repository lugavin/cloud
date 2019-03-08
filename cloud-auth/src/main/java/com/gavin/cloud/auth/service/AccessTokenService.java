package com.gavin.cloud.auth.service;

import com.gavin.cloud.auth.enums.AuthMessageType;
import com.gavin.cloud.common.base.dto.LoginUserDTO;
import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.common.base.jwt.Jwt;
import com.gavin.cloud.common.base.jwt.JwtVerifier;
import com.gavin.cloud.common.base.jwt.Payload;
import com.gavin.cloud.common.web.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.gavin.cloud.common.base.jwt.cipher.Algorithm.Type.RS256;

@Service
public class AccessTokenService {

    @Autowired
    private JwtProperties jwtProperties;

    private final byte[] privateKey;

    public AccessTokenService(@Value("${app.jwt.privateKey}") String privateKey) {
        this.privateKey = Base64.getUrlDecoder().decode(privateKey);
    }

    public String createToken(LoginUserDTO loginUserDTO) {
        long currTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return Jwt.builder()
                .withIss("https://auth.gavin.com")
                .withSub(loginUserDTO.getUid())
                .withIat(Long.toString(currTimeSeconds))
                .withExp(Long.toString(currTimeSeconds + jwtProperties.getValidityInSeconds()))
                .with("ip", loginUserDTO.getClientIP())
                .with("username", loginUserDTO.getUsername())
                .sign(privateKey, RS256);
    }

    public LoginUserDTO verifyToken(String token) {
        JwtVerifier verifier = Jwt.verifier(token);
        if (verifier.verify(jwtProperties.getPublicKey())) {
            Payload payload = verifier.getPayload();
            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) <= Long.parseLong(payload.getExp())) {
                Map<String, String> map = payload.getMap();
                return new LoginUserDTO(payload.getSub(), map.get("ip"), map.get("username"));
            }
        }
        throw new AppException(AuthMessageType.ERR_UNAUTHORIZED);
    }

}
