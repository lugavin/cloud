package com.gavin.cloud.search.core.repository;

import com.gavin.cloud.search.core.model.Item;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveElasticsearchRepository<Item, Long> {

    Flux<Item> findByPriceBetween(double from, double to);

}
