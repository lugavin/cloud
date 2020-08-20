package com.gavin.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 注解@SpringCloudApplication = @SpringBootApplication + @EnableDiscoveryClient + @EnableCircuitBreaker
 * (1)@EnableDiscoveryClient: 在服务启动时触发服务注册的过程, 向配置文件中指定的服务注册中心(Eureka Server)地址注册自己提供的服务.
 * (2)@EnableCircuitBreaker: 服务熔断处理(只作用在服务调用的一端), 避免因调用其他服务故障而形成任务积压最终导致自身服务瘫痪的现象.
 * (3)@EnableFeignClients: 开启Feign(Feign是一个声明式的WebService客户端)功能, 使得调用远程服务就像调用本地服务一样简单, 并且做到了客户端均衡负载.
 */
@EnableFeignClients
@SpringCloudApplication
public class AuthApp {

    public static void main(String[] args) {
        SpringApplication.run(AuthApp.class, args);
    }

}
