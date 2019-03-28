package com.gavin.cloud.distributed.jwt.cipher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class SignerVerifierFactory {

    private static List<SignerVerifierHandler> signerVerifierHandlers = Arrays.asList(
            new MACSignerVerifierHandler(),
            new RSASignerVerifierHandler()
    );

    public static List<SignerVerifierHandler> getSignerVerifierHandler() {
        return Collections.unmodifiableList(signerVerifierHandlers);
    }

}
