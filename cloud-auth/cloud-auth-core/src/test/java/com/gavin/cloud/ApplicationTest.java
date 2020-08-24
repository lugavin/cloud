package com.gavin.cloud;

import com.gavin.cloud.auth.core.mapper.AuthTokenMapper;
import com.gavin.cloud.auth.core.mapper.ext.AuthTokenMapperExt;
import com.gavin.cloud.auth.pojo.AuthToken;
import com.gavin.cloud.auth.pojo.AuthTokenExample;
import com.gavin.cloud.common.base.page.Page;
import com.gavin.cloud.common.base.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
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

import java.time.Instant;
import java.util.*;

import static com.gavin.cloud.common.base.util.Constants.PROFILE_DEV;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * 当在Junit单元测试类中加了{@link SpringBootTest}注解时, 如果你的单元测试方法上加了{@link Transactional}注解,
 * 默认情况下它会在每个测试方法结束时回滚事务, 以防止测试数据对数据库造成污染, 如果想要测试数据不回滚, 可以加上{@link Rollback}注解并设置值为false.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(PROFILE_DEV)
public class ApplicationTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private AuthTokenMapperExt authTokenMapperExt;

    @Before
    public void setUp() {
        Assert.assertNotNull(sqlSessionFactory);
    }

    /**
     * 测试一级缓存: SqlSession级别
     */
    @Test
    public void testPrimaryCache1() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AuthTokenMapper mapper = sqlSession.getMapper(AuthTokenMapper.class);
            log.info("{}", mapper.selectByPrimaryKey(101L)); // 发出SQL语句
            log.info("{}", mapper.selectByPrimaryKey(101L)); // 不发出SQL语句
        }
    }

    @Test
    public void testPrimaryCache2() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AuthTokenMapper mapper = sqlSession.getMapper(AuthTokenMapper.class);
            log.info("{}", mapper.selectByPrimaryKey(101L)); // 发出SQL语句
            sqlSession.commit(); // 清空一级缓存
            log.info("{}", mapper.selectByPrimaryKey(101L)); // 发出SQL语句
        }
    }

    /**
     * 测试二级缓存: Mapper级别(基于Namespace跨SqlSession)
     */
    @Test
    public void testSecondaryCache1() {
        try (SqlSession sqlSession1 = sqlSessionFactory.openSession();
             SqlSession sqlSession2 = sqlSessionFactory.openSession()) {
            AuthTokenMapper mapper1 = sqlSession1.getMapper(AuthTokenMapper.class);
            log.info("{}", mapper1.selectByPrimaryKey(101L)); // 发出SQL语句
            sqlSession1.commit(); // 执行SELECT的commit操作会将SqlSession中的数据存入二级缓存区域
            AuthTokenMapper mapper2 = sqlSession2.getMapper(AuthTokenMapper.class);
            log.info("{}", mapper2.selectByPrimaryKey(101L)); // 不发出SQL语句
        }
    }

    @Test
    public void testSecondaryCache2() {
        try (SqlSession sqlSession1 = sqlSessionFactory.openSession();
             SqlSession sqlSession2 = sqlSessionFactory.openSession()) {
            AuthTokenMapper mapper1 = sqlSession1.getMapper(AuthTokenMapper.class);
            AuthToken token = mapper1.selectByPrimaryKey(101L); // 发出SQL语句
            log.info("{}", token);
            token.setExpiredAt(Date.from(Instant.now().plus(7, DAYS)));
            mapper1.updateByPrimaryKey(token);
            sqlSession1.commit(); // 当执行了非SELECT语句时整个namespace中的缓存会被清空
            AuthTokenMapper mapper2 = sqlSession2.getMapper(AuthTokenMapper.class);
            log.info("{}", mapper2.selectByPrimaryKey(101L)); // 发出SQL语句
        }
    }

    @Test
    public void testSelectByExample() {
        Date sysTime = Calendar.getInstance().getTime();
        AuthTokenExample example = new AuthTokenExample();
        AuthTokenExample.Criteria criteria = example.createCriteria();
        criteria.andCreatedAtBetween(Date.from(sysTime.toInstant().minus(3, DAYS)), sysTime);
        List<AuthToken> list = authTokenMapperExt.selectByExample(example);
        log.info(JsonUtils.toJson(list));
    }

    /**
     * 测试Mapper继承
     */
    @Test
    public void testExtMapper() {
        Optional.ofNullable(authTokenMapperExt.selectByPrimaryKey(101L))
                .ifPresent(r -> log.info("====== {} ======", JsonUtils.toJson(r)));
    }

    /**
     * 测试分页拦截器
     */
    @Test
    public void testPageInterceptor() {
        Page<AuthToken> page = authTokenMapperExt.getPage(Collections.singletonMap("uid", 111L), 1, 10);
        log.info("====== {} ======", JsonUtils.toJson(page));
    }

}
