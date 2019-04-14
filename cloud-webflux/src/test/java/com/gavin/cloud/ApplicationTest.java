package com.gavin.cloud;

import com.gavin.cloud.distributed.RemoteService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTest {

    @Autowired
    private RemoteService remoteService;

    @Before
    public void setUp() {
        Assert.assertNotNull(remoteService);
    }

    @Test
    public void testRetry() {
        remoteService.call();
    }

}
