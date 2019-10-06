package com.gavin.cloud.excel;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ListSplitTest {

    private static final BigDecimal FILE_MAX_AMT = new BigDecimal(20);
    private static final Integer FILE_MAX_NUM = 10;

    @Data
    private static class Model {
        private final BigDecimal allocAmt;
        private final Integer splitNum;
    }

    private static List<List<Model>> splitWithFileMaxAmt(List<Model> list) {
        List<List<Model>> lists = new ArrayList<>();
        List<Model> subList = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (Model model : list) {
            BigDecimal allocAmt = model.getAllocAmt();
            if (allocAmt.compareTo(FILE_MAX_AMT) > 0) {
                throw new RuntimeException("商户待划款金额 " + allocAmt + " 大于文件最大金额 " + FILE_MAX_AMT);
            }
            sum = sum.add(allocAmt);
            if (sum.compareTo(FILE_MAX_AMT) > 0) {
                if (!subList.isEmpty()) {
                    lists.add(subList);
                    subList = new ArrayList<>();
                }
                subList.add(model);
                sum = allocAmt;
                continue;
            }
            subList.add(model);
        }
        if (!subList.isEmpty()) {
            lists.add(subList);
        }
        return lists;
    }

    private static List<List<Model>> splitWithFileMaxNum(List<Model> list) {
        List<List<Model>> lists = new ArrayList<>();
        List<Model> subList = new ArrayList<>();
        int sum = 0;
        for (Model model : list) {
            int splitNum = model.getSplitNum();
            if (splitNum > FILE_MAX_NUM) {
                throw new RuntimeException("拆分笔数 " + splitNum + " 大于文件最大笔数 " + FILE_MAX_NUM);
            }
            sum += splitNum;
            if (sum > FILE_MAX_NUM) {
                if (!subList.isEmpty()) {
                    lists.add(subList);
                    subList = new ArrayList<>();
                }
                subList.add(model);
                sum = splitNum;
                continue;
            }
            subList.add(model);
        }
        if (!subList.isEmpty()) {
            lists.add(subList);
        }
        return lists;
    }

    private static BigDecimal reduce(BigDecimal... items) {
        return Stream.of(items).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static void main(String[] args) {
        List<Model> models = Arrays.asList(
                new Model(new BigDecimal("5.12"), 3),
                new Model(new BigDecimal("8.12"), 5),
                new Model(new BigDecimal("4.12"), 3),
                new Model(new BigDecimal("6.12"), 4),
                new Model(new BigDecimal("1.12"), 1),
                new Model(new BigDecimal("1.12"), 1)
        );
        List<List<Model>> lists = new ArrayList<>();
        splitWithFileMaxAmt(models).stream().map(ListSplitTest::splitWithFileMaxNum).forEach(lists::addAll);
        lists.forEach(System.err::println);
    }
}