package com.gavin.cloud;

import com.gavin.cloud.common.base.util.JsonUtils;
import com.gavin.cloud.sys.core.dao.ext.PermissionExtDao;
import com.gavin.cloud.sys.core.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.gavin.cloud.common.base.util.Constants.PROFILE_DEV;

/**
 * 当在Junit单元测试类中加了{@link SpringBootTest}注解时, 如果你的单元测试方法上加了{@link Transactional}注解,
 * 默认情况下它会在每个测试方法结束时回滚事务, 以防止测试数据对数据库造成污染, 如果想要测试数据不回滚, 可以加上{@link Rollback}注解并设置值为false.
 */
@Slf4j
@SpringBootTest
@ActiveProfiles(PROFILE_DEV)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final String ROLE_ADMIN = "admin";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PermissionExtDao permissionExtMapper;

    @Autowired
    private PermissionService permissionService;

    @Before
    public void setUp() {
        Assert.assertNotNull(stringRedisTemplate);
        Assert.assertNotNull(permissionExtMapper);
        Assert.assertNotNull(permissionService);
    }

    @Test
    public void testRedis() {
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        permissionExtMapper.getPermsByRole(ROLE_ADMIN)
                .forEach(permission -> opsForHash.put(ROLE_ADMIN, permission.getCode(), JsonUtils.toJson(permission)));
        log.debug("{}", opsForHash.keys(ROLE_ADMIN));
    }

    @Test
    public void testGetPermissionCodes() {
        Set<String> permCodes = permissionService.getPermissionCodes(ROLE_ADMIN);
        log.debug("{}", permCodes);
    }

}
