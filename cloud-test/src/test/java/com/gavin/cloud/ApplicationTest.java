package com.gavin.cloud;

import com.gavin.cloud.retry.RetryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ApplicationTest {

    @Autowired
    private RetryService retryService;

    @BeforeEach
    public void setUp() {
        assertNotNull(retryService);
    }

    @Test
    public void testRetry() {
        retryService.call();
    }

}
