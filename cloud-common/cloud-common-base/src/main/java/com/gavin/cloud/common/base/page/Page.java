package com.gavin.cloud.common.base.page;

import java.util.Iterator;
import java.util.List;

/**
 * A page is a sublist of a list of objects. It allows gain information about the position of it in the containing
 * entire list.
 *
 * @param <T>
 * @author Gavin
 */
public interface Page<T> extends Iterable<T> {

    /**
     * Returns the number of the current {@link Page}. Is always non-negative.
     *
     * @return the number of the current {@link Page}.
     */
    int getPage();

    /**
     * Returns the size of the {@link Page}.
     *
     * @return the size of the {@link Page}.
     */
    int getPageSize();

    /**
     * @return the page content as {@link List}.
     */
    List<T> getItems();

    /**
     * Returns the total amount of elements.
     *
     * @return the total amount of elements
     */
    long getTotalItems();

    /**
     * Returns the number of total pages.
     *
     * @return the number of total pages
     */
    default long getTotalPages() {
        return (getTotalItems() + getPageSize() - 1) / getPageSize();
    }

    /**
     * Returns the offset to be taken according to the underlying page and page size.
     *
     * @return the offset to be taken
     */
    default int getOffset() {
        return (getPage() - 1) * getPageSize();
    }

    /**
     * Returns the number of elements currently on this {@link Page}.
     *
     * @return the number of elements currently on this {@link Page}.
     */
    default int getItemsSize() {
        return getItems().size();
    }

    /**
     * @return whether the current {@link Page} is the first one.
     */
    default boolean isFirst() {
        return !hasPrevious();
    }

    /**
     * @return whether the current {@link Page} is the last one.
     */
    default boolean isLast() {
        return !hasNext();
    }

    /**
     * Returns if there is a previous {@link Page}.
     *
     * @return if there is a previous {@link Page}.
     */
    default boolean hasPrevious() {
        return getPage() > 1;
    }

    /**
     * Returns if there is a next {@link Page}.
     *
     * @return if there is a next {@link Page}.
     */
    default boolean hasNext() {
        return getPage() < getTotalPages();
    }

    @Override
    default Iterator<T> iterator() {
        return getItems().iterator();
    }

}
