package com.gavin.cloud.common.base.problem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.gavin.cloud.common.base.problem.AlertType.ALERT_KEY_MESSAGE;
import static com.gavin.cloud.common.base.problem.AlertType.ALERT_KEY_PARAMS;

/**
 * Custom parameterized exception which can be translated on the client side.
 * For example:
 *
 * <pre>
 * throw new AppParamedException("myCustomError", "hello", "world");
 * </pre>
 * <p>
 * Can be translated with:
 *
 * <pre>
 * "error.myCustomError" :  "The server says {{param0}} to {{param1}}"
 * </pre>
 */
public class AppParamedException extends AbstractThrowableProblem {

    private static final String PARAM = "param";
    private static final URI PARAMETERIZED_TYPE = URI.create(PROBLEM_BASE_URL + "/parameterized");

    public AppParamedException(String errorKey, String... params) {
        this(errorKey, toParamMap(params));
    }

    public AppParamedException(String errorKey, Map<String, Object> paramMap) {
        super(PARAMETERIZED_TYPE, HttpStatus.BAD_REQUEST, "Parameterized Exception", null, null, toProblemParameters(errorKey, paramMap));
    }

    private static Map<String, Object> toParamMap(String... params) {
        Map<String, Object> paramMap = new HashMap<>();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                paramMap.put(PARAM + i, params[i]);
            }
        }
        return paramMap;
    }

    private static Map<String, Object> toProblemParameters(String errorKey, Map<String, Object> paramMap) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ALERT_KEY_MESSAGE, "error." + errorKey);
        parameters.put(ALERT_KEY_PARAMS, paramMap);
        return parameters;
    }
}