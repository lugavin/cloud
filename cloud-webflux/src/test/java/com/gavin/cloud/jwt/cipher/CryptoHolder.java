package com.gavin.cloud.jwt.cipher;

public class CryptoHolder {

    private final String alg;
    private final byte[] key;
    private final byte[] data;

    public CryptoHolder(String alg, byte[] key, byte[] data) {
        this.alg = alg;
        this.key = key;
        this.data = data;
    }

    public String getAlg() {
        return alg;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getData() {
        return data;
    }

}