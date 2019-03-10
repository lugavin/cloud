package com.gavin.cloud.common.base.jwt.cipher;

public interface Signer extends Algorithm {

    byte[] sign(byte[] data);

}