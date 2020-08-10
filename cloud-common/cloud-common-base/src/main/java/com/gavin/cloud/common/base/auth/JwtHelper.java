package com.gavin.cloud.common.base.auth;

import com.gavin.cloud.common.base.problem.AuthenticationException;
import com.gavin.cloud.common.base.util.JsonUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class JwtHelper {

    private static final String CLAIM_KEY_USERNAME = "username";
    private static final String CLAIM_KEY_CLIENT_IP = "client_ip";
    private static final String CLAIM_KEY_ROLES = "roles";

    public static String createToken(@NonNull ActiveUser activeUser,
                                     @NonNull PrivateKey privateKey,
                                     @NonNull Integer validityInSeconds) {
        Calendar calendar = Calendar.getInstance();
        Date iat = calendar.getTime();
        calendar.add(Calendar.SECOND, validityInSeconds);
        Date exp = calendar.getTime();
        return Jwts.builder()
                .serializeToJsonWith(map -> JsonUtils.toJson(map).getBytes(UTF_8))
                .setIssuedAt(iat)
                .setExpiration(exp)
                .setSubject(Long.toString(activeUser.getUid()))
                .claim(CLAIM_KEY_USERNAME, activeUser.getUsername())
                .claim(CLAIM_KEY_CLIENT_IP, activeUser.getClientIP())
                .claim(CLAIM_KEY_ROLES, activeUser.getRoles())
                .signWith(privateKey)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public static ActiveUser verifyToken(@NonNull String token, @NonNull PublicKey publicKey) throws AuthenticationException {
        try {
            Claims claims = Jwts.parser()
                    .deserializeJsonWith(bytes -> JsonUtils.fromJson(new String(bytes, UTF_8), Object.class))
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token)
                    .getBody();
            //OK, we can trust this JWT
            return ActiveUser.builder()
                    .uid(Long.parseLong(claims.getSubject()))
                    .username((String) claims.get(CLAIM_KEY_USERNAME))
                    .clientIP((String) claims.get(CLAIM_KEY_CLIENT_IP))
                    .roles(claims.get(CLAIM_KEY_ROLES, ArrayList.class))
                    .build();
        } catch (Exception e) {
            //Don't trust the JWT!
            throw new AuthenticationException("The token is illegal.");
        }
    }

    // public static void main(String[] args) throws Exception {
    //     KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    //     keyPairGenerator.initialize(2048);
    //     KeyPair keyPair = keyPairGenerator.generateKeyPair();
    //     System.err.println(Base64.getUrlEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
    //     System.err.println(Base64.getUrlEncoder().encodeToString(keyPair.getPublic().getEncoded()));
    //     ActiveUser activeUser = ActiveUser.builder()
    //             .uid(101L)
    //             .username("admin")
    //             .clientIP("127.0.0.1")
    //             .roles(Arrays.asList("user:create", "user:delete"))
    //             .build();
    //     String token = createToken(activeUser, keyPair.getPrivate(), 300);
    //     System.out.println(token);
    //     System.out.println(verifyToken(token, keyPair.getPublic()));
    // }

}
