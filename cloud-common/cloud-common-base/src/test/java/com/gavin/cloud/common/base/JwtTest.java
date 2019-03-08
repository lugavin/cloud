package com.gavin.cloud.common.base;

import com.gavin.cloud.common.base.jwt.Jwt;
import com.gavin.cloud.common.base.jwt.JwtVerifier;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
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
 * @see <a href="https://jwt.io/">JWT</a>
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
        //System.out.printf(PUBLIC_KEY_FORMAT, Base64.getUrlEncoder().encodeToString(publicKey.getEncoded())).println();
        //System.out.printf(PRIVATE_KEY_FORMAT, Base64.getUrlEncoder().encodeToString(privateKey.getEncoded())).println();
    }

    @Test
    public void testJwtWithHS384() {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker();
        long uid = idWorker.nextId();
        long currTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        byte[] key = "1111111111111111".getBytes(StandardCharsets.UTF_8);
        String token = Jwt.builder()
                .withIss("https://auth.gavin.com")
                .withSub(Long.toString(uid))
                .withIat(Long.toString(currTimeSeconds))
                .withExp(Long.toString(currTimeSeconds + 1800))
                .with("ip", "192.168.1.1")
                .with("username", "admin")
                .sign(key, HS384);
        System.out.println(token);
        JwtVerifier verifier = Jwt.verifier(token);
        System.out.println(verifier.getPayload().getSub());
        System.out.println(verifier.getPayload().getMap());
        Assert.assertTrue(verifier.verify(key));
    }

    @Test
    public void testJwtWithRS384() throws Exception {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker();
        long uid = idWorker.nextId();
        long currTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        String pubKey = Base64.getUrlEncoder().encodeToString(publicKey.getEncoded());
        String priKey = Base64.getUrlEncoder().encodeToString(privateKey.getEncoded());
        String token = Jwt.builder()
                .withIss("https://auth.gavin.com")
                .withSub(Long.toString(uid))
                .withIat(Long.toString(currTimeSeconds))
                .withExp(Long.toString(currTimeSeconds + 1800))
                .with("ip", "192.168.1.1")
                .with("username", "admin")
                .sign(Base64.getUrlDecoder().decode(priKey), RS384);
        System.out.println(token);
        JwtVerifier verifier = Jwt.verifier(token);
        System.out.println(verifier.getPayload().getSub());
        System.out.println(verifier.getPayload().getMap());
        Assert.assertTrue(verifier.verify(Base64.getUrlDecoder().decode(pubKey)));
    }

}
