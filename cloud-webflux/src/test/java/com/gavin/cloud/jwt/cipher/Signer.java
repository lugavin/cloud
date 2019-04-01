package com.gavin.cloud.jwt.cipher;

public interface Signer extends Algorithm {

    byte[] sign(byte[] data);

}