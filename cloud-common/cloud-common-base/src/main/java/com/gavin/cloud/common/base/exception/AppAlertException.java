package com.gavin.cloud.common.base.exception;

import java.util.HashMap;
import java.util.Map;

public class AppAlertException extends AbstractThrowableProblem {

    public AppAlertException(AlertType alertType) {
        this(alertType, null);
    }

    public AppAlertException(AlertType alertType, String detail) {
        this(alertType, detail, null);
    }

    public AppAlertException(AlertType alertType, String detail, ThrowableProblem cause) {
        this(alertType, detail, cause, getAlertParameters(alertType));
    }

    private AppAlertException(AlertType alertType, String detail, ThrowableProblem cause, Map<String, Object> parameters) {
        super(alertType.getType(), HttpStatus.BAD_REQUEST, alertType.getTitle(), detail, cause, parameters);
    }

    private static Map<String, Object> getAlertParameters(AlertType alertType) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "error." + alertType.getErrorKey());
        parameters.put("params", alertType.getEntityName());
        return parameters;
    }

}
