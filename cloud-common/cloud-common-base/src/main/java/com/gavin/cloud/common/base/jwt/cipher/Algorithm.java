package com.gavin.cloud.common.base.jwt.cipher;

/**
 * HMAC是密钥相关的哈希运算消息认证码, HMAC运算利用哈希算法, 以一个密钥和一个消息为输入, 生成一个消息摘要作为输出.
 * <p>
 * HS256和RS256情景:
 * (1)这些算法不用于加密/解密数据而是用于验证数据的来源或真实性.
 * (2)HS256可以使用单个密钥为给定的数据样本创建签名, 当消息与签名一起传输时, 接收方可以使用相同的密钥来验证签名是否与消息匹配.
 * (3)RS256使用一对钥匙来做同样的事情, 签名只能使用私钥生成, 并且必须使用公钥来验证签名.
 *
 * @author Gavin Lu
 */
public interface Algorithm {

    enum Type {

        HS256("HmacSHA256"),
        HS384("HmacSHA384"),
        HS512("HmacSHA512"),
        RS256("SHA256withRSA"),
        RS384("SHA384withRSA"),
        RS512("SHA512withRSA");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

    }

    static Type forType(final String alg) {
        for (Type type : Type.values()) {
            if (type.name().equalsIgnoreCase(alg)) {
                return type;
            }
        }
        return null;
    }

    String getAlgorithm();

}