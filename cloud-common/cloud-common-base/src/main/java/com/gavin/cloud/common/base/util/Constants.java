package com.gavin.cloud.common.base.util;

public interface Constants {

    String PROFILE_DEV = "dev";
    String PROFILE_PROD = "prod";
    String PROFILE_SWAGGER = "swagger";

    String DATE_FORMAT = "yyyy-MM-dd";
    String TIME_FORMAT = "HH:mm:ss";
    String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String SEPARATOR_SLASHES = "/";
    String SEPARATOR_PERIOD = ".";
    String SEPARATOR_COMMA = ",";
    String SEPARATOR_SEMICOLON = ";";
    String SEPARATOR_UNDERSCORE = "_";

    String EMPTY = "";
    String SPACE = " ";

    String CHARSET_UTF_8 = "UTF-8";
    String DEFAULT_LANGUAGE = "zh-cn";

    String SYSTEM_ACCOUNT = "system";
    String ANONYMOUS_USER = "anonymoususer";

    String REGEX_LOGIN_NAME = "^[_.@A-Za-z0-9-]*$";
    String REGEX_LOGIN_TYPE = "1|2|3";

}
