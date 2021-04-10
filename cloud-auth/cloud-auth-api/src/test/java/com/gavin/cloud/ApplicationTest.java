package com.gavin.cloud;

import com.gavin.cloud.client.GitHubClient;
import com.gavin.cloud.client.RemoteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private GitHubClient client;

    @Autowired
    private RemoteService remoteService;

    @BeforeEach
    public void setUp() {
        assertNotNull(client);
        assertNotNull(remoteService);
    }

    @Test
    public void testRestTemplate() {
        String json = remoteService.getUser("lugavin");
        log.info("====== {} ======", json);
    }

    @Test
    public void testFeignClient() {
        String json = client.getUser("lugavin");
        log.info("====== {} ======", json);
    }

}
