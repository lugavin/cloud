package com.gavin.cloud.common.base.util;

import org.apache.commons.lang3.RandomStringUtils;

public abstract class RandomUtils extends RandomStringUtils {

    private static final int DEF_COUNT = 19;

    public static String randomNumeric() {
        return randomNumeric(DEF_COUNT);
    }

    public static String randomAlphabetic() {
        return randomAlphabetic(DEF_COUNT);
    }

    public static String randomAlphanumeric() {
        return randomAlphanumeric(DEF_COUNT);
    }

}
