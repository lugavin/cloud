package com.gavin.cloud.common.core.plugin;

import com.gavin.cloud.common.base.page.PageImpl;
import com.gavin.cloud.common.base.page.Pageable;
import com.gavin.cloud.common.core.dialect.Dialect;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Intercepts({@Signature(type = Executor.class, method = "query", args = {
        MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class PageInterceptor implements Interceptor {

    private static final String COUNT_SUFFIX = "Count";

    private static final int MAPPED_STATEMENT_INDEX = 0;
    private static final int PARAMETER_INDEX = 1;
    private static final int ROW_BOUNDS_INDEX = 2;
    private static final int RESULT_HANDLER_INDEX = 3;

    private final Dialect dialect;

    public PageInterceptor(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        final Object parameter = args[PARAMETER_INDEX];
        Executor executor = (Executor) invocation.getTarget();
        if (parameter instanceof Pageable) {
            Pageable<?> pageable = (Pageable<?>) parameter;
            int page = pageable.getPage();
            int pageSize = pageable.getPageSize();
            final MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
            final ResultHandler<?> resultHandler = (ResultHandler<?>) args[RESULT_HANDLER_INDEX];
            final BoundSql boundSql = ms.getBoundSql(parameter);

            Counter counter = new Counter();
            String countSql = dialect.getCountString(boundSql.getSql());
            MappedStatement countStatement = newCountMappedStatement(ms, boundSql, countSql);
            executor.query(countStatement, boundSql.getParameterObject(), RowBounds.DEFAULT, rc -> counter.value = (long) rc.getResultObject());

            String limitSql = dialect.getLimitString(boundSql.getSql(), (page - 1) * pageSize, pageSize);
            MappedStatement limitStatement = newLimitMappedStatement(ms, boundSql, limitSql);
            List<?> items = executor.query(limitStatement, parameter, RowBounds.DEFAULT, resultHandler);

            //String limitSql = dialect.getLimitString(boundSql.getSql(), (page - 1) * pageSize, pageSize);
            //args[ROW_BOUNDS_INDEX] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
            //args[MAPPED_STATEMENT_INDEX] = newLimitMappedStatement(ms, boundSql, limitSql);
            //List<?> items = (List<?>) invocation.proceed();

            return Collections.singletonList(new PageImpl<>(page, pageSize, items, counter.value));
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

    private MappedStatement newCountMappedStatement(MappedStatement ms, BoundSql boundSql, String countSql) {
        String countStatementId = ms.getId() + COUNT_SUFFIX;
        List<ResultMap> countResultMaps = Collections.singletonList(new ResultMap.Builder(ms.getConfiguration(), countStatementId, Long.class, Collections.emptyList()).build());
        return newMappedStatement(ms, boundSql, countSql, countStatementId, countResultMaps);
    }

    private MappedStatement newLimitMappedStatement(MappedStatement ms, BoundSql boundSql, String limitSql) {
        return newMappedStatement(ms, boundSql, limitSql, ms.getId(), ms.getResultMaps());
    }

    private MappedStatement newMappedStatement(MappedStatement ms, BoundSql boundSql, String sql, String statementId, List<ResultMap> resultMaps) {
        return new MappedStatement.Builder(ms.getConfiguration(), statementId, p -> newBoundSql(ms, boundSql, sql), ms.getSqlCommandType())
                .resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .statementType(ms.getStatementType())
                .keyGenerator(ms.getKeyGenerator())
                .keyProperty(Objects.nonNull(ms.getKeyProperties()) ? String.join(",", ms.getKeyProperties()) : null)
                .timeout(ms.getTimeout())
                .parameterMap(ms.getParameterMap())
                .resultMaps(resultMaps)
                .resultSetType(ms.getResultSetType())
                .cache(ms.getCache())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .build();
    }

    private BoundSql newBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
        final BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        boundSql.getParameterMappings().stream()
                .map(ParameterMapping::getProperty)
                .filter(boundSql::hasAdditionalParameter)
                .forEach(p -> newBoundSql.setAdditionalParameter(p, boundSql.getAdditionalParameter(p)));
        return newBoundSql;
    }

    private static class Counter {
        private long value;
    }

}
