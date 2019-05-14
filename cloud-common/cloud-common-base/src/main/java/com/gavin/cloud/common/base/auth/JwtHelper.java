package com.gavin.cloud.common.base.auth;

import com.gavin.cloud.common.base.problem.AuthenticationException;
import com.gavin.cloud.common.base.util.JsonUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @see <a href="https://github.com/jhipster/generator-jhipster/blob/master/generators/server/templates/src/main/java/package/web/filter/RefreshTokenFilter.java.ejs">RefreshTokenFilter</a>
 */
public abstract class JwtHelper {

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
                .claim("roles", activeUser.getRoles())
                .claim("username", activeUser.getUsername())
                .claim("client_ip", activeUser.getClientIP())
                .serializeToJsonWith(map -> JsonUtils.toJson(map).getBytes(UTF_8))
                .signWith(privateKey, SignatureAlgorithm.RS256)
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
            String uid = claims.getSubject();
            String username = (String) claims.get("username");
            String clientIP = (String) claims.get("client_ip");
            List<String> roles = claims.get("roles", ArrayList.class);
            return new ActiveUser(Long.parseLong(uid), username, clientIP, roles);
        } catch (Exception e) {
            //Don't trust the JWT!
            throw new AuthenticationException("The token is illegal.");
        }
    }

    //public static void main(String[] args) {
    //    KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
    //    System.out.println(Base64.getUrlEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
    //    System.out.println(Base64.getUrlEncoder().encodeToString(keyPair.getPublic().getEncoded()));
    //}

}
