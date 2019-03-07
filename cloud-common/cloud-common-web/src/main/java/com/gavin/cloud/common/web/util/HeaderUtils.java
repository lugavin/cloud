package com.gavin.cloud.common.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
@Slf4j
public final class HeaderUtils {

    private static final String X_APP_ALERT = "X-App-Alert";
    private static final String X_APP_ERROR = "X-App-Error";
    private static final String X_APP_PARAMS = "X-App-Params";

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert(entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert(entityName + ".updated", param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert(entityName + ".deleted", param);
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_APP_ALERT, message);
        headers.add(X_APP_PARAMS, param);
        return headers;
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        log.error("Entity processing failed, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        //headers.add(X_APP_ERROR, "error." + errorKey);
        headers.add(X_APP_ERROR, errorKey);
        headers.add(X_APP_PARAMS, entityName);
        return headers;
    }
}
