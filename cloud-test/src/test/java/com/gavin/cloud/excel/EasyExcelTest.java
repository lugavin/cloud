package com.gavin.cloud.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import lombok.Data;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EasyExcelTest {

    private String classpath;

    @Before
    public void setUp() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        Assert.assertNotNull(url);
        classpath = url.getPath();
        System.err.println(classpath);
    }

    @Test
    public void testWriteWithoutTemplate() {
        String fileName = classpath + System.currentTimeMillis() + ".xlsx";
        WriteCellStyle headWriteCellStyle = new WriteCellStyle(); // 设置表头样式
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 16);
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle(); // 设置单元格内容样式
        EasyExcel.write(fileName, Model.class)
                .registerWriteHandler(new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle))
                .sheet(0, "工作表1")
                .doWrite(data());
    }

    @Test
    public void testWriteWithTemplate() {
        String templateFileName = classpath + "template.xlsx";
        String fileName = classpath + System.currentTimeMillis() + ".xlsx";
        List<Model> list1 = data();
        BigDecimal sum = list1.stream().map(Model::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Sum> list2 = Collections.singletonList(new Sum(list1.size(), sum));
        ExcelWriter excelWriter = EasyExcel.write(fileName, Serializable.class).withTemplate(templateFileName).build();
        excelWriter.write(list1, EasyExcel.writerSheet(0).build());
        excelWriter.write(list2, EasyExcel.writerSheet(1).build());
        excelWriter.finish();
    }

    private List<Model> data() {
        List<Model> list = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            list.add(new Model("6222xxxxxxxxxxxxxxx" + String.format("%03d", i), new BigDecimal(i)));
        }
        return list;
    }

    @Data
    public static class Model {
        @ColumnWidth(30)
        @ExcelProperty(value = "账户", index = 0)
        private final String acc;
        @ExcelProperty(value = "金额", index = 1)
        private final BigDecimal amt;
    }

    @Data
    public static class Sum {
        private final Integer count;
        private final BigDecimal amt;
    }

}
