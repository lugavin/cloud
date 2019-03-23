package com.gavin.cloud.common.base.problem;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom parameterized exception which can be translated on the client side.
 * For example:
 *
 * <pre>
 * throw new CustomParameterizedException("myCustomError", "hello", "world");
 * </pre>
 *
 * Can be translated with:
 *
 * <pre>
 * "error.myCustomError" :  "The server says {{param0}} to {{param1}}"
 * </pre>
 */
public class CustomParameterizedException extends AbstractThrowableProblem {

    private static final String PARAM = "param";

    public CustomParameterizedException(String message, String... params) {
        this(message, toParamMap(params));
    }

    public CustomParameterizedException(String message, Map<String, Object> paramMap) {
        super(ProblemType.PARAMETERIZED_TYPE, "Parameterized Exception", Status.BAD_REQUEST, null, null, null, toProblemParameters(message, paramMap));
    }

    public static Map<String, Object> toParamMap(String... params) {
        Map<String, Object> paramMap = new HashMap<>();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                paramMap.put(PARAM + i, params[i]);
            }
        }
        return paramMap;
    }

    public static Map<String, Object> toProblemParameters(String message, Map<String, Object> paramMap) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", message);
        parameters.put("params", paramMap);
        return parameters;
    }
}