package com.gavin.cloud.common.base.exception;

import java.net.URI;

public interface AlertType {

    URI getType();

    String getTitle();

    String getEntityName();

    String getErrorKey();

}
