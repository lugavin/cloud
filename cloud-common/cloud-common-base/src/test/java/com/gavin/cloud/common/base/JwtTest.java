package com.gavin.cloud.common.base;

import com.gavin.cloud.common.base.jwt.Jwt;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.gavin.cloud.common.base.jwt.cipher.Algorithm.Type.HS384;
import static com.gavin.cloud.common.base.jwt.cipher.Algorithm.Type.RS384;

/**
 * @see <a href="https://www.jsonwebtoken.io/">JWT</a>
 */
public class JwtTest {

    private static final String PUBLIC_KEY_FORMAT = "-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY_FORMAT = "-----BEGIN RSA PRIVATE KEY-----\n%s\n-----END RSA PRIVATE KEY-----";

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @Before
    public void setUp() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL key = Objects.requireNonNull(classLoader.getResource("cert.jks"));
        privateKey = exportPrivateKeyFromKeystore(key.getFile(), "gavin", "P@ssw0rd");
        URL cert = Objects.requireNonNull(classLoader.getResource("cert.cer"));
        publicKey = extractPublicKeyFromCertificate(cert.getFile());
        System.out.printf(PUBLIC_KEY_FORMAT, Base64.getEncoder().encodeToString(publicKey.getEncoded())).println();
        System.out.printf(PRIVATE_KEY_FORMAT, Base64.getEncoder().encodeToString(privateKey.getEncoded())).println();
    }

    @Test
    public void testJwtWithRS384() {
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

    @Test
    public void testRSASignAndVerify() {
        byte[] data = "This is the data that needs to be encrypted.".getBytes();
        byte[] signed = sign(data);
        boolean verified = verify(data, signed);
        Assert.assertTrue(verified);
    }

    /**
     * Sign with RSA private key
     */
    private byte[] sign(byte[] bytes) {
        try {
            Signature signature = Signature.getInstance(RS384.value());
            signature.initSign(privateKey);
            signature.update(bytes);
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verify with RSA public key
     */
    private boolean verify(byte[] content, byte[] sig) {
        try {
            Signature signature = Signature.getInstance(RS384.value());
            signature.initVerify(publicKey);
            signature.update(content);
            return signature.verify(sig);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 从证书中提取公钥
     *
     * @param certificateFile certificate file
     * @return PublicKey
     */
    private PublicKey extractPublicKeyFromCertificate(String certificateFile) {
        try (FileInputStream in = new FileInputStream(certificateFile)) {
            CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
            X509Certificate Cert = (X509Certificate) certificatefactory.generateCertificate(in);
            return Cert.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从密钥库中导出私钥
     *
     * @param keystoreFile keystore file
     * @param alias        alias
     * @param keypass      keypass
     * @return PrivateKey
     */
    private PrivateKey exportPrivateKeyFromKeystore(String keystoreFile, String alias, String keypass) {
        try (FileInputStream in = new FileInputStream(keystoreFile)) {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(in, keypass.toCharArray());
            return (PrivateKey) ks.getKey(alias, keypass.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
