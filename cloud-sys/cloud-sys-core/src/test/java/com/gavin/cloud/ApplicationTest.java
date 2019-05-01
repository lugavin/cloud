package com.gavin.cloud;

import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.base.util.JsonUtils;
import com.gavin.cloud.sys.core.mapper.UserMapper;
import com.gavin.cloud.sys.core.mapper.ext.PermissionExtMapper;
import com.gavin.cloud.sys.core.mapper.ext.UserExtMapper;
import com.gavin.cloud.sys.pojo.User;
import com.gavin.cloud.sys.pojo.UserExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 当在Junit单元测试类中加了{@link SpringBootTest}注解时, 如果你的单元测试方法上加了{@link Transactional}注解,
 * 默认情况下它会在每个测试方法结束时回滚事务, 以防止测试数据对数据库造成污染, 如果想要测试数据不回滚, 可以加上{@link Rollback}注解并设置值为false.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(Constants.PROFILE_DEV)
public class ApplicationTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private UserExtMapper userExtMapper;

    @Autowired
    private PermissionExtMapper permissionExtMapper;

    @Before
    public void setUp() {
        Assert.assertNotNull(sqlSessionFactory);
    }

    /**
     * 测试一级缓存
     */
    @Test
    public void testPrimaryCache1() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            log.info("{}", userMapper.selectByPrimaryKey(101L)); // 发出SQL语句
            log.info("{}", userMapper.selectByPrimaryKey(101L)); // 不发出SQL语句
        }
    }

    @Test
    public void testPrimaryCache2() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            log.info("{}", userMapper.selectByPrimaryKey(101L)); // 发出SQL语句
            sqlSession.commit();                                    // 清空缓存
            log.info("{}", userMapper.selectByPrimaryKey(101L)); // 发出SQL语句
        }
    }

    /**
     * 测试二级缓存
     */
    @Test
    public void testSecondaryCache1() {
        try (SqlSession sqlSession1 = sqlSessionFactory.openSession();
             SqlSession sqlSession2 = sqlSessionFactory.openSession()) {

            UserMapper userMapper1 = sqlSession1.getMapper(UserMapper.class);
            log.info("{}", userMapper1.selectByPrimaryKey(101L)); // 发出SQL语句
            sqlSession1.commit(); // 执行SELECT的commit操作会将SqlSession中的数据存入二级缓存区域

            UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);
            log.info("{}", userMapper2.selectByPrimaryKey(101L)); // 不发出SQL语句
        }
    }

    @Test
    public void testSecondaryCache2() {
        try (SqlSession sqlSession1 = sqlSessionFactory.openSession();
             SqlSession sqlSession2 = sqlSessionFactory.openSession()) {

            UserMapper userMapper1 = sqlSession1.getMapper(UserMapper.class);
            User user = userMapper1.selectByPrimaryKey(101L); // 发出SQL语句
            log.info("{}", user);
            user.setEmail("admin@gmail.com");
            userMapper1.updateByPrimaryKey(user);
            sqlSession1.commit(); // 当执行了非SELECT语句时整个namespace中的缓存会被清空

            UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);
            log.info("{}", userMapper2.selectByPrimaryKey(101L)); // 发出SQL语句
        }
    }

    /**
     * 测试分页拦截器
     */
    @Test
    public void testPageInterceptor() {
        Map<String, Object> param = new HashMap<>();
        param.put("nickname", "管理员");
        Page<User> page = userExtMapper.getPage(param, 1, 10);
        log.info("====== {} ======", JsonUtils.toJson(page));
    }

    /**
     * 测试Mapper继承
     */
    @Test
    public void testExtMapper() {
        Optional.ofNullable(permissionExtMapper.selectByPrimaryKey(11L))
                .ifPresent(r -> log.info("====== {} ======", JsonUtils.toJson(r)));
    }

    @Test
    public void testSelectByExample() {
        Date sysTime = Calendar.getInstance().getTime();
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andActivatedNotEqualTo(Boolean.TRUE);
        criteria.andCreatedAtBetween(DateUtils.addDays(sysTime, -3), sysTime);
        List<User> users = userExtMapper.selectByExample(example);
        log.info(JsonUtils.toJson(users));
    }

    @Test
    public void testCollection() {
        List<String> newRoles = Arrays.asList("admin", "user");
        List<String> oldRoles = Arrays.asList("user", "guest");
        List<String> deleteRoles = new ArrayList<>(oldRoles);
        deleteRoles.removeAll(newRoles);
        log.info("=== deleteRoles:{} ===", deleteRoles);
        List<String> insertRoles = new ArrayList<>(newRoles);
        insertRoles.removeAll(oldRoles);
        log.info("=== insertRoles:{} ===", insertRoles);
    }

}
