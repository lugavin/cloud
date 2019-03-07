package com.gavin.cloud.search.core.repository;

import com.gavin.cloud.search.core.model.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import java.util.List;

public interface ItemRepository extends ElasticsearchCrudRepository<Item, Long> {

    List<Item> findByPriceBetween(double from, double to);

}
