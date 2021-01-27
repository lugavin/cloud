package com.gavin.cloud.common.base.auth;

import com.gavin.cloud.common.base.problem.AppBizException;
import com.gavin.cloud.common.base.util.JsonUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

import static com.gavin.cloud.common.base.problem.DefaultProblemType.AUTHENTICATION_FAILED_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * JWT无法作废未过期的TOKEN解决方案:
 * (1)RefreshToken(推荐): 生成AccessToken(有效期短)的同时生成RefreshToken(有效期长)
 * (2)Redis(不推荐): 登录时将JWT(exp=24h)的失效时间设置到Redis(expire=600s), 校验JWT时刷新Redis的失效时间
 */
public abstract class JwtHelper {

    private static final String ALGORITHM_RSA = "RSA";

    private static final String CLAIM_KEY_USERNAME = "username";
    private static final String CLAIM_KEY_CLIENT_IP = "client_ip";
    private static final String CLAIM_KEY_ROLES = "roles";

    public static String createToken(ActiveUser activeUser, String privateKeyEncoded, Long validityInSeconds) {
        return createToken(activeUser, createPrivateKey(privateKeyEncoded), validityInSeconds);
    }

    public static String createToken(ActiveUser activeUser, Key privateKey, Long validityInSeconds) {
        return Jwts.builder()
                .serializeToJsonWith(map -> JsonUtils.toJson(map).getBytes(UTF_8))
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(validityInSeconds)))
                .setSubject(Long.toString(activeUser.getUid()))
                .claim(CLAIM_KEY_USERNAME, activeUser.getUsername())
                .claim(CLAIM_KEY_CLIENT_IP, activeUser.getClientIP())
                .claim(CLAIM_KEY_ROLES, activeUser.getRoles())
                .signWith(privateKey)
                .compact();
    }

    public static ActiveUser verifyToken(String token, String publicKeyEncoded) {
        return verifyToken(token, createPublicKey(publicKeyEncoded));
    }

    @SuppressWarnings("unchecked")
    public static ActiveUser verifyToken(String token, Key publicKey) {
        try {
            Claims claims = Jwts.parser()
                    .deserializeJsonWith(bytes -> JsonUtils.fromJson(new String(bytes, UTF_8), Map.class, String.class, Object.class))
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token)
                    .getBody();
            //OK, we can trust this JWT
            return new ActiveUser(Long.parseLong(claims.getSubject()), claims.get(CLAIM_KEY_USERNAME, String.class),
                    claims.get(CLAIM_KEY_CLIENT_IP, String.class), claims.get(CLAIM_KEY_ROLES, ArrayList.class));
        } catch (Exception e) {
            //Don't trust the JWT!
            throw new AppBizException(AUTHENTICATION_FAILED_TYPE, "The token is illegal.");
        }
    }

    private static PrivateKey createPrivateKey(String privateKeyEncoded) {
        try {
            return KeyFactory.getInstance(ALGORITHM_RSA)
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getUrlDecoder().decode(privateKeyEncoded)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static PublicKey createPublicKey(String publicKeyEncoded) {
        try {
            return KeyFactory.getInstance(ALGORITHM_RSA)
                    .generatePublic(new X509EncodedKeySpec(Base64.getUrlDecoder().decode(publicKeyEncoded)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // public static void main(String[] args) throws Exception {
    //     KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    //     keyPairGenerator.initialize(2048);
    //     KeyPair keyPair = keyPairGenerator.generateKeyPair();
    //     String privateKeyEncoded = Base64.getUrlEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    //     String publicKeyEncoded = Base64.getUrlEncoder().encodeToString(keyPair.getPublic().getEncoded());
    //     ActiveUser activeUser = new ActiveUser(101L, "admin", "127.0.0.1", Arrays.asList("user:create", "user:delete"));
    //     String token = createToken(activeUser, privateKeyEncoded, 300L);
    //     System.err.println(verifyToken(token, publicKeyEncoded));
    // }

}
