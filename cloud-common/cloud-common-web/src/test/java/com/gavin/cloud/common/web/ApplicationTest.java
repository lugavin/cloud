package com.gavin.cloud.common.web;

import com.gavin.cloud.common.base.cache.CacheService;
import com.gavin.cloud.common.base.util.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @see <a href="http://www.importnew.com/19102.html">spring4新特性</a>
 * @see <a href="https://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/htmlsingle/">spring-framework-4</a>
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final String USER_HKEY = "USER";

    @Autowired
    private ApplicationContext context;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private List<CacheService> cacheServices;

    @Before
    public void setUp() throws Exception {
        Assert.assertNotNull(messageSource);
        cacheServices = new ArrayList<>(context.getBeansOfType(CacheService.class).values());
    }

    @Test
    public void testRedis() throws Exception {

        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        Map<String, String> hash = new HashMap<>();
        hash.put("id", RandomUtils.randomUUID());
        hash.put("name", "gavin");

        opsForHash.putAll(USER_HKEY, hash);
        redisTemplate.expire(USER_HKEY, 30, TimeUnit.SECONDS);

        String name = opsForHash.get(USER_HKEY, "name");
        Assert.assertTrue("gavin".equals(name));
    }

    @Test
    public void testInjection() throws Exception {
        cacheServices.forEach(System.err::println);
    }

}
