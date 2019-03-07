package com.gavin.cloud.common.base.exception;

import java.util.Map;

public interface Exceptional {

    /**
     * @return the HTTP status code
     */
    Status getStatus();

    /**
     * @return a short, human-readable summary of this exception
     */
    String getTitle();

    /**
     * @return A human readable explaination of this problem
     */
    String getDetail();

    /**
     * Optional, additional attributes of the problem. Implementations can choose to ignore this in favor of concrete,
     * typed fields.
     *
     * @return additional parameters
     */
    Map<String, Object> getParameters();

    /**
     * @return the current time in milliseconds.
     */
    default long getTimestamp() {
        return System.currentTimeMillis();
    }

}
