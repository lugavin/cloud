package com.gavin.cloud.jwt;

/**
 * The following fields can be used in authentication headers:
 * - typ(Token type): If present, it is recommended to set this to JWT.
 * - cty(Content type): If nested signing or encryption is employed, it is recommended to set this to JWT, otherwise omit this field.
 * - alg(algorithm): The issuer can freely set an algorithm to verify the signature on the token. However, some supported algorithms are insecure.
 * <p>
 * The Internet drafts define the following standard fields ("claims") that can be used inside a JWT claim set:
 * - iss(Issuer): identifies principal that issued the JWT;
 * - sub(Subject): identifies the subject of the JWT;
 * - aud(Audience): The "aud" (audience) claim identifies the recipients that the JWT is intended for. Each principal intended to process the JWT MUST identify itself with a value in the audience claim. If the principal processing the claim does not identify itself with a value in the aud claim when this claim is present, then the JWT MUST be rejected.
 * - exp(Expiration time): The "exp" (expiration time) claim identifies the expiration time on or after which the JWT MUST NOT be accepted for processing.
 * - nbf(Not before): Similarly, the not-before time claim identifies the time on which the JWT will start to be accepted for processing.
 * - iat(Issued at): The "iat" (issued at) claim identifies the time at which the JWT was issued.
 * - jti(JWT ID): case sensitive unique identifier of the token even among different issuers.
 */
public interface PublicClaim {

    enum Header {
        typ, alg, cty
    }

    enum Payload {
        iss, sub, exp, nbf, iat, jti, aud
    }

}
