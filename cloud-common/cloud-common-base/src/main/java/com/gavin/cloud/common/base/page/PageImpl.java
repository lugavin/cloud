package com.gavin.cloud.common.base.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Basic {@code Page} implementation.
 *
 * @param <T> the type of which the page consists.
 * @author Gavin
 */
public class PageImpl<T> implements Page<T> {

    /**
     * 传递的参数
     * page: 当前页
     * pageSize: 每页显示的记录数
     */
    private final int page;
    private final int pageSize;

    /**
     * 查询数据库
     * items: 当前页的数据列表
     * totalItems: 总记录数
     */
    private final List<T> items;
    private final int totalItems;

    /**
     * {@code PageImpl}.
     *
     * @param page       the number of the current {@link Page}.
     * @param pageSize   the size of the {@link Page}.
     * @param items      the content of this page.
     * @param totalItems the total amount of elements available.
     */
    public PageImpl(int page, int pageSize, List<T> items, int totalItems) {
        this.page = Math.max(page, 1);
        this.pageSize = Math.max(pageSize, 1);
        this.items = Optional.ofNullable(items).orElseGet(ArrayList::new);
        this.totalItems = totalItems;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public int getTotalItems() {
        return totalItems;
    }

}
