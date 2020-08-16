package com.gavin.cloud.common.base.exception;

import java.net.URI;

public interface ProblemType {

    URI getType();

    HttpStatus getStatus();

    String getTitle();

}
