package com.gavin.cloud.sys.api;

import com.gavin.cloud.sys.api.model.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 在Spring Cloud中服务之间通过RESTful方式调用有两种方式:
 * - RestTemplate + Ribbon
 * - Feign
 * ZUUL也有负载均衡的功能, 它是针对外部请求做负载均衡, 而Ribbon是对服务之间调用做负载, 是服务之间的负载均衡.
 * <p>
 * 使用Feign时需要注意的问题:
 * - {@link FeignClient}接口不能使用{@link GetMapping}之类的组合注解;
 * - {@link FeignClient}接口中如果使用到{@link PathVariable}必须指定value值;
 */
public interface UserApi {

    @PostMapping("/users")
    User createUser(@RequestBody User user);

    @GetMapping("/users/{id}")
    User getUser(@PathVariable("id") String id);

    @GetMapping("/users/{account}/{type}")
    User getUser(@PathVariable("account") String account, @PathVariable("type") int type);

}
