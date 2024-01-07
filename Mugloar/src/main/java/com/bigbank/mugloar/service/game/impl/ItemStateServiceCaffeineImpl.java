package com.bigbank.mugloar.service.game.impl;

import com.bigbank.mugloar.dto.domain.core.ItemDto;
import com.bigbank.mugloar.service.game.ItemStateService;
import com.bigbank.mugloar.util.Consts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemStateServiceCaffeineImpl implements ItemStateService {

    @CachePut(value = Consts.ITEMS_AVAILABLE_CACHE, key = "#gameId")
    public Map<String, ItemDto> initAvailableItems(String gameId, Map<String, ItemDto> availableItems){
        log.info("Loaded {} available items for Game: {} ",availableItems.size(), gameId);
        return availableItems;
    }

    @CachePut(value = Consts.ITEMS_PRIORITIZED_CACHE, key = "#gameId")
    public Queue<String> initPrioritizedItemKeys(String gameId, Queue<String> prioritizedItems) {
        log.info("Loaded {} prioritized items Keys for Game: {} ",prioritizedItems.size(), gameId);
        return prioritizedItems;
    }
    @Cacheable(value = Consts.ITEMS_PRIORITIZED_CACHE, key = "#gameId")
    public Queue<String> getPrioritizedItemKeys(String gameId){
        throw new IllegalStateException(format("No Prioritized Items found in cache for Game: %s ",gameId));
    }

    @Override
    @Cacheable(value = Consts.ITEMS_AVAILABLE_CACHE, key = "#gameId")
    public Map<String,ItemDto> getAvailableItems(String gameId){
        throw new IllegalStateException(format("No Available Items found in cache for Game: %s ",gameId));
    }



    @CachePut(value = Consts.ITEMS_PRIORITIZED_CACHE, key = "#gameId")
    public Queue<String> reloadPrioritizedItemKeys(String gameId,Queue<String> prioritizedItems){
        log.info("Reloaded {} prioritized items Keys for Game: {} ",prioritizedItems.size(), gameId);
        return prioritizedItems;
    }

}
