package com.gavin.cloud.common.base.problem;

import java.util.Map;

/**
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807: Problem Details for HTTP APIs</a>
 */
public interface Problem {

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

    static ExceptionalBuilder builder() {
        return new ExceptionalBuilder();
    }

}
