package com.gavin.cloud.distributed.jwt.cipher;

public interface Signer extends Algorithm {

    byte[] sign(byte[] data);

}