package com.gavin.cloud.jwt;

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

    /*
    public <T> T get(String claimName, Class<T> requiredType) {
        Object value = map.get(claimName);
        if (value == null) {
            return null;
        }
        if (Date.class.equals(requiredType)) {
            return (T) DatatypeConverter.parseDateTime(String.valueOf(value)).getTime();
        }
        return castClaimValue(value, requiredType);
    }

    private <T> T castClaimValue(Object value, Class<T> requiredType) {
        if (value instanceof Integer) {
            int intValue = (Integer) value;
            if (requiredType == Long.class) {
                value = (long) intValue;
            } else if (requiredType == Short.class && Short.MIN_VALUE <= intValue && intValue <= Short.MAX_VALUE) {
                value = (short) intValue;
            } else if (requiredType == Byte.class && Byte.MIN_VALUE <= intValue && intValue <= Byte.MAX_VALUE) {
                value = (byte) intValue;
            }
        }
        if (!requiredType.isInstance(value)) {
            throw new IllegalArgumentException("Expected value to be of type: " + requiredType + ", but was " + value.getClass());
        }
        return requiredType.cast(value);
    }
    */

}
