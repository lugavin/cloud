package com.gavin.cloud.common.base.util;

import org.apache.commons.lang3.RandomStringUtils;

public abstract class RandomUtils {

    private static final int DEF_COUNT = 19;

    public static String randomNumeric() {
        return randomNumeric(DEF_COUNT);
    }

    public static String randomNumeric(int count) {
        return RandomStringUtils.randomNumeric(count);
    }

    public static String randomAlphabetic() {
        return randomAlphabetic(DEF_COUNT);
    }

    public static String randomAlphabetic(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static String randomAlphanumeric() {
        return randomAlphanumeric(DEF_COUNT);
    }

    public static String randomAlphanumeric(int count) {
        return RandomStringUtils.randomAlphanumeric(count);
    }

}
