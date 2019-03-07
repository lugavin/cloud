package com.gavin.cloud.common.base.page;

public interface Pageable<T> {

    T getParam();

    int getPage();

    int getPageSize();

}
