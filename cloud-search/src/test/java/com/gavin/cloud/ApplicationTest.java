package com.gavin.cloud;

import com.gavin.cloud.search.core.repository.ItemRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import com.gavin.cloud.search.core.model.Item;
import reactor.core.publisher.Flux;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        Assert.assertNotNull(itemRepository);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAddData() {
        List<Item> items = Arrays.asList(
                new Item(1L, "苹果手机", "手机", "苹果", 4999.00, "https://images.emall.com/201902220001.jpg"),
                new Item(2L, "苹果笔记本", "笔记本", "苹果", 9999.00, "https://images.emall.com/201902220002.jpg"),
                new Item(3L, "苹果照相机", "苹果照相机", "苹果", 5999.00, "https://images.emall.com/201902220003.jpg"),
                new Item(4L, "索尼手机", "手机", "索尼", 2999.00, "https://images.emall.com/201902220004.jpg"),
                new Item(5L, "索尼电视机", "电视机", "索尼", 4999.00, "https://images.emall.com/201902220005.jpg")
        );
        Flux<Item> flux = itemRepository.saveAll(items);
        flux.toIterable(10).forEach(System.out::println);
    }

    @Test
    public void testFindAll() {
        Flux<Item> flux = itemRepository.findAll();
        flux.toIterable(10).forEach(System.out::println);
    }

}