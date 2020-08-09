package com.gavin.cloud.common.base.auth;

import com.gavin.cloud.common.base.problem.AuthenticationException;
import com.gavin.cloud.common.base.util.JsonUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

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
                .setIssuedAt(iat)
                .setExpiration(exp)
                .setSubject(Long.toString(activeUser.getUid()))
                .claim(CLAIM_KEY_USERNAME, activeUser.getUsername())
                .claim(CLAIM_KEY_CLIENT_IP, activeUser.getClientIP())
                .claim(CLAIM_KEY_ROLES, activeUser.getRoles())
                .serializeToJsonWith(map -> JsonUtils.toJson(map).getBytes(UTF_8))
                .signWith(privateKey)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public static ActiveUser verifyToken(@NonNull String token, @NonNull PublicKey publicKey) throws AuthenticationException {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .deserializeJsonWith(bytes -> JsonUtils.fromJson(new String(bytes, UTF_8), Map.class))
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

    // public static void main(String[] args) {
    //     KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
    //     PrivateKey privateKey = keyPair.getPrivate();
    //     PublicKey publicKey = keyPair.getPublic();
    //     System.out.println(Base64.getUrlEncoder().encodeToString(privateKey.getEncoded()));
    //     System.out.println(Base64.getUrlEncoder().encodeToString(publicKey.getEncoded()));
    //     ActiveUser activeUser = ActiveUser.builder()
    //             .uid(101L)
    //             .username("admin")
    //             .clientIP("127.0.0.1")
    //             .roles(Arrays.asList("user:create", "user:delete"))
    //             .build();
    //     String token = createToken(activeUser, privateKey, 300);
    //     System.err.println(token);
    //     System.err.println(verifyToken(token, publicKey));
    // }

}
