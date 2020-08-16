package com.gavin.cloud.common.web.problem;

import java.net.URI;

public interface ProblemType {

    String PROBLEM_BASE_URL = "http://www.lugavin.com/problem";
    URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    URI PARAMETERIZED_TYPE = URI.create(PROBLEM_BASE_URL + "/parameterized");
    URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/contraint-violation");

}
