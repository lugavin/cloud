package com.gavin.cloud.common.base;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Objects;

public class CertTest {

    private static final String RS384 = "SHA384withRSA";

    private static final String PUBLIC_KEY_FORMAT = "-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY_FORMAT = "-----BEGIN PRIVATE KEY-----\n%s\n-----END PRIVATE KEY-----";

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
            Signature signature = Signature.getInstance(RS384);
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
            Signature signature = Signature.getInstance(RS384);
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
