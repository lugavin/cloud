package com.gavin.cloud.jwt;

import java.util.Collections;
import java.util.Map;

public class Header {

    public static final String JWT_TYPE = "JWT";

    private final String typ = JWT_TYPE;
    private final String alg;
    private final Map<String, String> map;

    public Header(Map<String, String> map) {
        String tokenType = map.remove(PublicClaim.Header.typ.name());
        if (tokenType != null && !tokenType.equalsIgnoreCase(typ)) {
            throw new IllegalArgumentException("typ is not \"JWT\"");
        }
        String alg = map.remove(PublicClaim.Header.alg.name());
        if (alg == null) {
            throw new IllegalArgumentException("alg is required");
        }
        map.remove(PublicClaim.Header.cty.name());
        this.alg = alg;
        this.map = map;
    }


    public String getTyp() {
        return typ;
    }

    public String getAlg() {
        return alg;
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(map);
    }

}
