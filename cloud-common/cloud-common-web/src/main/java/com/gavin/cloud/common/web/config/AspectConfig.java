package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.web.aop.logging.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import static com.gavin.cloud.common.base.util.Constants.PROFILE_DEV;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    @Profile(PROFILE_DEV)
    public LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }

    // @Bean
    // @Profile(PROFILE_DEV)
    // public ValidationAspect validationAspect() {
    //     return new ValidationAspect();
    // }

}
