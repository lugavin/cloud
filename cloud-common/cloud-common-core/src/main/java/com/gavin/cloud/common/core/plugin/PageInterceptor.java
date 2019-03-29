package com.gavin.cloud.common.core.plugin;

import com.gavin.cloud.common.base.page.PageImpl;
import com.gavin.cloud.common.base.page.Pageable;
import com.gavin.cloud.common.base.util.JsonUtils;
import com.gavin.cloud.common.core.dialect.Database;
import com.gavin.cloud.common.core.dialect.Dialect;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Intercepts({@Signature(type = Executor.class, method = "query", args = {
        MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class PageInterceptor implements Interceptor {

    private static final String SPACE = " ";
    private static final String MULTI_SPACE_PATTERN = " +";
    private static final String NEWLINE_PATTERN = "[\\n\\r\\t]";

    private static final int MAPPED_STATEMENT_INDEX = 0;
    private static final int PARAMETER_INDEX = 1;
    private static final int ROW_BOUNDS_INDEX = 2;
    private static final int RESULT_HANDLER_INDEX = 3;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        final Object parameter = args[PARAMETER_INDEX];
        if (parameter instanceof Pageable) {
            Pageable<?> pageable = (Pageable<?>) parameter;
            final MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
            final BoundSql boundSql = ms.getBoundSql(parameter);
            int page = pageable.getPage();
            int pageSize = pageable.getPageSize();

            DialectCountHolder holder = getCount(ms, boundSql);
            Dialect dialect = holder.getDialect();
            int totalItems = holder.getCount();

            String limitSql = dialect.getLimitString(boundSql.getSql(), (page - 1) * pageSize, pageSize);
            args[ROW_BOUNDS_INDEX] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
            args[MAPPED_STATEMENT_INDEX] = newMappedStatement(ms, boundSql, limitSql);
            List<?> items = (List<?>) invocation.proceed();

            return Collections.singletonList(new PageImpl<>(page, pageSize, items, totalItems));
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * @see org.springframework.jdbc.core.JdbcTemplate#execute
     * @see DataSourceUtils
     * @see <a href="https://www.ibm.com/developerworks/cn/java/j-lo-spring-ts1/">Spring Transaction Manager</a>
     */
    private DialectCountHolder getCount(MappedStatement ms, BoundSql boundSql) throws SQLException {
        DataSource dataSource = ms.getConfiguration().getEnvironment().getDataSource();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // 首先尝试从事务上下文中获取连接, 失败后再从数据源获取连接
            conn = DataSourceUtils.getConnection(dataSource);
            Dialect dialect = getDatabaseDialect(conn);
            String countSql = dialect.getCountString(boundSql.getSql());
            stmt = conn.prepareStatement(countSql);
            log.debug("==>  Preparing: {}", countSql.replaceAll(NEWLINE_PATTERN, SPACE).replaceAll(MULTI_SPACE_PATTERN, SPACE));
            BoundSql countBoundSql = newBoundSql(ms, boundSql, countSql);
            ParameterHandler handler = new DefaultParameterHandler(ms, boundSql.getParameterObject(), countBoundSql);
            handler.setParameters(stmt);
            log.debug("==> Parameters: {}", JsonUtils.toJson(boundSql.getParameterObject()));
            rs = stmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            log.debug("<==      Total: {}", count);
            return new DialectCountHolder(dialect, count);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(stmt);
            // 释放连接, 放回到连接池中
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    private MappedStatement newMappedStatement(MappedStatement ms, BoundSql boundSql, String sql) {
        return newMappedStatement(ms, p -> newBoundSql(ms, boundSql, sql));
    }

    private BoundSql newBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
        final BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    /**
     * @see org.apache.ibatis.builder.MapperBuilderAssistant
     */
    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource sqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), sqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    /**
     * @see org.apache.ibatis.mapping.VendorDatabaseIdProvider#getDatabaseProductName
     */
    private Dialect getDatabaseDialect(Connection conn) throws SQLException {
        String dbType = conn.getMetaData().getDatabaseProductName();
        return Optional.ofNullable(Database.fromType(dbType))
                .orElseThrow(() -> new RuntimeException("Unsupported database type: " + dbType))
                .getDialect();
    }

    private static class DialectCountHolder {

        private final Dialect dialect;
        private final int count;

        public DialectCountHolder(Dialect dialect, int count) {
            this.dialect = dialect;
            this.count = count;
        }

        public Dialect getDialect() {
            return dialect;
        }

        public int getCount() {
            return count;
        }

    }

}
