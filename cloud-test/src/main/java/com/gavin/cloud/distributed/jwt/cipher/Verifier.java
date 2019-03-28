package com.gavin.cloud.distributed.jwt.cipher;

public interface Verifier extends Algorithm {

    boolean verify(byte[] data, byte[] sig);

}
