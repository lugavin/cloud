package com.gavin.cloud;

import com.gavin.cloud.search.core.repository.ItemRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.gavin.cloud.search.core.model.Item;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Before
    public void setUp() throws Exception {
        Objects.requireNonNull(esTemplate);
        Objects.requireNonNull(itemRepository);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateIndex() throws Exception {
        // 创建索引库
        esTemplate.createIndex(Item.class);
        // 映射关系
        esTemplate.putMapping(Item.class);
    }

    @Test
    public void testAddData() throws Exception {
        List<Item> items = Arrays.asList(
                new Item(1L, "苹果手机", "手机", "苹果", 4999.00, "https://images.emall.com/201902220001.jpg"),
                new Item(2L, "苹果笔记本", "笔记本", "苹果", 9999.00, "https://images.emall.com/201902220002.jpg"),
                new Item(3L, "苹果照相机", "苹果照相机", "苹果", 5999.00, "https://images.emall.com/201902220003.jpg"),
                new Item(4L, "索尼手机", "手机", "索尼", 2999.00, "https://images.emall.com/201902220004.jpg"),
                new Item(5L, "索尼电视机", "电视机", "索尼", 4999.00, "https://images.emall.com/201902220005.jpg")
        );
        Iterable<Item> iterable = itemRepository.saveAll(items);
        iterable.forEach(System.out::println);
    }

    @Test
    public void testFindAll() throws Exception {
        Iterable<Item> iterable = itemRepository.findAll();
        iterable.forEach(System.out::println);
    }

    @Test
    public void testFindBy() throws Exception {
        Iterable<Item> iterable = itemRepository.findByPriceBetween(4000d, 5000d);
        iterable.forEach(System.out::println);
    }

}