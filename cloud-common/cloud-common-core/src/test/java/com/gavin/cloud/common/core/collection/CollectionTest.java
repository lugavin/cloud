package com.gavin.cloud.common.core.collection;

import com.gavin.cloud.common.core.model.Counter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class CollectionTest {

    private List<Counter> list1 = new ArrayList<>();
    private List<Counter> list2 = new ArrayList<>();

    @Before
    public void setUp() {
        initData();
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

    @Test
    public void testGetCreatedData() {
        log.info("====== {} ======", getCreatedData(list1, list2));
    }

    @Test
    public void testGetDeletedData() {
        log.info("====== {} ======", getDeletedData(list1, list2));
    }

    @Test
    public void testGetUnmodifiedData() {
        log.info("====== {} ======", getUnmodifiedData(list1, list2));
    }

    @Test
    public void testGetModifiedData() {
        log.info("====== {} ======", getModifiedData(list1, list2));
    }

    private void initData() {
        Date sysdate = Calendar.getInstance().getTime();
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Counter counter = new Counter();
            counter.setId((long) i);
            counter.setTitle("test_" + i);
            counter.setCreatedAt(sysdate);
            counter.setCreatedBy("system");
            counter.setUpdatedAt(sysdate);
            counter.setCreatedBy("system");
            list1.add(counter);
        });
        IntStream.rangeClosed(5, 15).forEach(i -> {
            Counter counter = new Counter();
            counter.setId((long) i);
            counter.setTitle("test_" + i);
            counter.setCreatedAt(sysdate);
            counter.setCreatedBy("system");
            counter.setUpdatedAt(sysdate);
            counter.setCreatedBy("system");
            list2.add(counter);
        });
    }

    private List<Counter> getCreatedData(List<Counter> list1, List<Counter> list2) {
        List<Counter> list = new ArrayList<>(list1);
        list.removeAll(list2);
        return Collections.unmodifiableList(list);
    }

    private List<Counter> getDeletedData(List<Counter> list1, List<Counter> list2) {
        List<Counter> list = new ArrayList<>(list2);
        list.removeAll(list1);
        return Collections.unmodifiableList(list);
    }

    private List<Counter> getUnmodifiedData(List<Counter> list1, List<Counter> list2) {
        List<Counter> list = new ArrayList<>(list1);
        list.retainAll(list2);
        return Collections.unmodifiableList(list);
    }

    private List<Counter> getModifiedData(List<Counter> list1, List<Counter> list2) {
        List<Counter> list = new ArrayList<>(list1);
        list.removeAll(getCreatedData(list1, list2));
        list.removeAll(getUnmodifiedData(list1, list2));
        return Collections.unmodifiableList(list);
    }

}
