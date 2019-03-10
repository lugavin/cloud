package com.gavin.cloud.common.base.jwt.cipher;

public interface Verifier extends Algorithm {

    boolean verify(byte[] data, byte[] sig);

}
