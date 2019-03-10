package com.gavin.cloud.common.base.builder;

import lombok.ToString;

@ToString
public class JwtHeader {

    public static final String JWT_TYPE = "JWT";

    private String typ = JWT_TYPE;
    private String cty;
    private String alg;

    public JwtHeader(String typ, String cty, String alg) {
        this.typ = typ;
        this.cty = cty;
        this.alg = alg;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getCty() {
        return cty;
    }

    public void setCty(String cty) {
        this.cty = cty;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public static JwtHeaderBuilder builder() {
        return new JwtHeaderBuilder();
    }

    public static class JwtHeaderBuilder {

        private String typ;
        private String cty;
        private String alg;

        public JwtHeaderBuilder typ(String typ) {
            this.typ = typ;
            return this;
        }

        public JwtHeaderBuilder cty(String cty) {
            this.cty = cty;
            return this;
        }

        public JwtHeaderBuilder alg(String alg) {
            this.alg = alg;
            return this;
        }

        public JwtHeader build() {
            return new JwtHeader(this.typ, this.cty, this.alg);
        }

    }

    public static void main(String[] args) {
        JwtHeader jwtHeader = JwtHeader.builder()
                .cty(JWT_TYPE)
                .typ(JWT_TYPE)
                .alg("RS256")
                .build();
        System.out.println(jwtHeader);
    }

}