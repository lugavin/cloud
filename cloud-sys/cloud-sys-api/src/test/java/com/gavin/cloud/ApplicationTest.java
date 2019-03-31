package com.gavin.cloud;

import com.gavin.cloud.client.GitHubClient;
import com.gavin.cloud.client.RemoteService;
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
    private GitHubClient client;

    @Autowired
    private RemoteService remoteService;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Before
    public void setUp() {
        Assert.assertNotNull(client);
        Assert.assertNotNull(remoteService);
        Assert.assertNotNull(loadBalancer);
    }

    @Test
    public void testRestTemplate() {
        Optional.ofNullable(remoteService.getUser("lugavin"))
                .ifPresent(System.err::println);
    }

    @Test
    public void testFeignClient() {
        Optional.ofNullable(client.getUser("lugavin"))
                .ifPresent(System.err::println);
    }

    @Test
    public void testLoadBalancer() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            ServiceInstance serviceInstance = loadBalancer.choose("stores");
            System.err.println(String.format("[%d] %s:%s", i, serviceInstance.getHost(), serviceInstance.getPort()));
        });
    }

}
