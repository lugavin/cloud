package com.gavin.cloud.jwt.cipher;

public interface Verifier extends Algorithm {

    boolean verify(byte[] data, byte[] sig);

}
