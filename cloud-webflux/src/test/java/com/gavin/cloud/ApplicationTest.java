package com.gavin.cloud;

import com.gavin.cloud.distributed.RemoteService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RemoteService remoteService;

    @Before
    public void setUp() {
        Assert.assertNotNull(amqpTemplate);
        Assert.assertNotNull(remoteService);
    }

    @Test
    public void testAMQP() throws Exception {
        amqpTemplate.convertAndSend("my-queue", "Hello RabbitMQ");
        TimeUnit.MILLISECONDS.sleep(50L);
    }

    @Test
    public void testRetry() {
        remoteService.call();
    }

}
