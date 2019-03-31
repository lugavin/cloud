package com.gavin.cloud;

import com.gavin.cloud.client.GitHubClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationTest {

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private GitHubClient client;

    @Before
    public void setUp() {
        Assert.assertNotNull(client);
    }

    @Test
    public void testLoadBalancer() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            ServiceInstance serviceInstance = loadBalancer.choose("stores");
            System.err.println(String.format("[%d] %s:%s", i, serviceInstance.getHost(), serviceInstance.getPort()));
        });
    }

    @Test
    public void testFeignClient() {
        Optional.ofNullable(client.getUser("lugavin"))
                .ifPresent(System.err::println);
    }

}
