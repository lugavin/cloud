package com.gavin.cloud.common.base;

import com.gavin.cloud.common.base.jwt.Jwt;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static com.gavin.cloud.common.base.jwt.cipher.Algorithm.Type.HS384;
import static com.gavin.cloud.common.base.jwt.cipher.Algorithm.Type.RS384;

/**
 * @see <a href="https://www.jsonwebtoken.io/">JWT</a>
 */
public class JwtTest {

    private static final String PUBLIC_KEY_FORMAT = "-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY_FORMAT = "-----BEGIN PRIVATE KEY-----\n%s\n-----END PRIVATE KEY-----";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Before
    public void setUp() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        System.out.printf(PUBLIC_KEY_FORMAT, Base64.getEncoder().encodeToString(publicKey.getEncoded())).println();
        System.out.printf(PRIVATE_KEY_FORMAT, Base64.getEncoder().encodeToString(privateKey.getEncoded())).println();
    }

    @Test
    public void testJwtWithRS384() throws Exception {
        String token = Jwt.builder()
                .withSub("admin")
                .withExp(Long.toString(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())))
                .with("email", "admin@gmail.com")
                .sign(privateKey.getEncoded(), RS384);
        System.out.println(token);
        boolean verified = Jwt.verifier(token).verify(publicKey.getEncoded());
        Assert.assertTrue(verified);
    }

    @Test
    public void testJwtWithHS384() {
        byte[] key = "1111111111111111".getBytes(StandardCharsets.UTF_8);
        String token = Jwt.builder()
                .withSub("admin")
                .withExp(Long.toString(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())))
                .with("email", "admin@gmail.com")
                .sign(key, HS384);
        System.out.println(token);
        boolean verified = Jwt.verifier(token).verify(key);
        Assert.assertTrue(verified);
    }

}
