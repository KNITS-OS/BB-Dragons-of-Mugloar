package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.dto.domain.core.ItemDto;

import java.util.Map;
import java.util.Queue;

public interface ItemStateService {
    Map<String, ItemDto> initAvailableItems(String gameId, Map<String, ItemDto> availableItems);
    Queue<String> initPrioritizedItemKeys(String gameId, Queue<String> prioritizedItems);
    Queue<String> getPrioritizedItemKeys(String gameId);
    Map<String,ItemDto> getAvailableItems(String gameId);
    Queue<String> reloadPrioritizedItemKeys(String gameId,Queue<String> prioritizedItems);

}
