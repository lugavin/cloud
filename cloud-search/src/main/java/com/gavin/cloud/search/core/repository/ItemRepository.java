package com.gavin.cloud.search.core.repository;

import com.gavin.cloud.search.core.model.Item;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Spring Data Elasticsearch repository for the Item entity.
 */
public interface ItemRepository extends ReactiveElasticsearchRepository<Item, Long>, ItemSearchRepositoryInternal {

}

interface ItemSearchRepositoryInternal {
    Flux<Item> search(String query);
}

class ItemSearchRepositoryInternalImpl implements ItemSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ItemSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Item> search(String query) {
        return reactiveElasticsearchTemplate.search(new NativeSearchQuery(queryStringQuery(query)), Item.class)
                .map(SearchHit::getContent);
    }

}
