package com.gavin.cloud.common.base.jwt;

import java.util.Collections;
import java.util.Map;

public class Payload {

    private final String iss;
    private final String sub;
    private final String exp;
    private final String nbf;
    private final String iat;
    private final String jti;
    private final String aud;
    private final Map<String, String> map;

    public Payload(Map<String, String> map) {
        this.iss = map.remove(PublicClaim.Payload.iss.name());
        this.sub = map.remove(PublicClaim.Payload.sub.name());
        this.exp = map.remove(PublicClaim.Payload.exp.name());
        this.nbf = map.remove(PublicClaim.Payload.nbf.name());
        this.iat = map.remove(PublicClaim.Payload.iat.name());
        this.jti = map.remove(PublicClaim.Payload.jti.name());
        this.aud = map.remove(PublicClaim.Payload.aud.name());
        this.map = map;
    }

    public String getIss() {
        return iss;
    }

    public String getSub() {
        return sub;
    }

    public String getExp() {
        return exp;
    }

    public String getNbf() {
        return nbf;
    }

    public String getIat() {
        return iat;
    }

    public String getJti() {
        return jti;
    }

    public String getAud() {
        return aud;
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(map);
    }

}
