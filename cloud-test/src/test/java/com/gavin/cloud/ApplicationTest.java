package com.gavin.cloud;

import com.gavin.cloud.retry.RetryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CloudApp.class)
public class ApplicationTest {

    @Autowired
    private RetryService retryService;

    @Before
    public void setUp() {
        Assert.assertNotNull(retryService);
    }

    @Test
    public void testRetry() {
        retryService.call();
    }

}
