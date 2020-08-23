package com.gavin.cloud.common.base.problem;

import java.net.URI;

public interface AlertType {

    String ALERT_KEY_MESSAGE = "message";
    String ALERT_KEY_PARAMS = "params";

    URI getType();

    String getTitle();

    String getEntityName();

    String getErrorKey();

}
