package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.base.problem.HttpStatus;
import com.gavin.cloud.common.web.config.jackson.ProblemModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Collections;

import static com.gavin.cloud.common.base.util.Constants.PROFILE_DEV;

@Configuration
public class JacksonConfig {

    private final Environment env;

    public JacksonConfig(Environment env) {
        this.env = env;
    }

    /**
     * Module for serialization/deserialization of RFC7807 Problem.
     */
    @Bean
    public ProblemModule problemModule() {
        return new ProblemModule(env.acceptsProfiles(PROFILE_DEV), Collections.singletonList(HttpStatus.class));
    }

}
