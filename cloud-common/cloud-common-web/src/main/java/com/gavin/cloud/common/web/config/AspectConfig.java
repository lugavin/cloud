package com.gavin.cloud.common.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    //@Bean
    //@Profile(Constants.ENV_DEV)
    //public LoggingAspect loggingAspect(Environment env) {
    //    return new LoggingAspect(env);
    //}

    //@Bean
    //@Profile({Constants.ENV_DEV})
    //public ValidationAspect validationAspect() {
    //    return new ValidationAspect();
    //}

}
