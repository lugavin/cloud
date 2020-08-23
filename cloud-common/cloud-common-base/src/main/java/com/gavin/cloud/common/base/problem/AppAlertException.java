package com.gavin.cloud.common.base.problem;

import java.util.HashMap;
import java.util.Map;

import static com.gavin.cloud.common.base.problem.AlertType.ALERT_KEY_MESSAGE;
import static com.gavin.cloud.common.base.problem.AlertType.ALERT_KEY_PARAMS;

public class AppAlertException extends AbstractThrowableProblem {

    private final AlertType alertType;

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
        this.alertType = alertType;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    private static Map<String, Object> getAlertParameters(AlertType alertType) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ALERT_KEY_MESSAGE, "error." + alertType.getErrorKey());
        parameters.put(ALERT_KEY_PARAMS, alertType.getEntityName());
        return parameters;
    }

}
