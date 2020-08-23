package com.gavin.cloud.common.base.problem;

import java.net.URI;

public interface ProblemType {

    URI getType();

    StatusType getStatus();

    String getTitle();

}
