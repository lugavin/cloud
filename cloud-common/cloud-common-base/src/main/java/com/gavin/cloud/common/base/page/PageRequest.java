package com.gavin.cloud.common.base.page;

public class PageRequest<T> implements Pageable<T> {

    private final int page;           // 当前页
    private final int pageSize;       // 每页显示的记录数
    private final T param;            // 额外参数

    public PageRequest(T param, int page, int pageSize) {
        this.param = param;
        this.page = Math.max(page, 1);
        this.pageSize = Math.max(pageSize, 1);
    }

    @Override
    public T getParam() {
        return param;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

}
