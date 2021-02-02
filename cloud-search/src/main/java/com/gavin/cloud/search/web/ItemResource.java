package com.gavin.cloud.search.web;

import com.gavin.cloud.search.core.model.Item;
import com.gavin.cloud.search.core.repository.ItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemResource {

    private final ItemRepository itemRepository;

    public ItemResource(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * {@code SEARCH /_search/items/:query} : search for the Item corresponding to the query.
     *
     * @param query the query to search.
     * @return the result of the search.
     */
    @GetMapping("/_search/items/{query}")
    public Mono<List<Item>> search(@PathVariable String query) {
        return itemRepository.search(query).collectList();
    }

}
