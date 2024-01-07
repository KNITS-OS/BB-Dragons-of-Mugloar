package com.bigbank.mugloar.service.core;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.dto.api.external.domugloar.ItemResponse;
import com.bigbank.mugloar.dto.api.external.domugloar.PurchaseItemRequest;
import com.bigbank.mugloar.dto.api.external.domugloar.PurchaseItemResponse;
import com.bigbank.mugloar.dto.domain.core.*;
import com.bigbank.mugloar.exception.GameOverException;
import com.bigbank.mugloar.exception.ItemBuyException;
import com.bigbank.mugloar.mappers.ItemMapper;
import com.bigbank.mugloar.mappers.PurchasedItemMapper;
import com.bigbank.mugloar.model.Item;
import com.bigbank.mugloar.model.PurchasedItem;
import com.bigbank.mugloar.proxy.ItemProxy;
import com.bigbank.mugloar.repository.core.ItemRepository;
import com.bigbank.mugloar.repository.core.PurchasedItemRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemService {

    private final ApplicationProperties appConfig;
    private final ItemProxy itemProxy;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final PurchasedItemRepository purchasedItemRepository;
    private final PurchasedItemMapper purchasedItemMapper;

    public List<ItemResponse> listItems (String gameId){
        return itemProxy.listItems(gameId);
    }


    public PurchaseItemResponse purchaseItem(PurchaseItemRequest request) {
        try {
            log.info("Executing Request to buy Item  {} ...", request.getItemId());
            ThreadUtils.sleepQuietly(Duration.ofMillis(appConfig.getThrottling().getApiThrottlingDelay()));
            return itemProxy.purchaseItem(request.getGameId(), request.getItemId());

        }  catch (FeignException e) {
            return handleRetry(e, request);
        }
    }

    private PurchaseItemResponse handleRetry(FeignException e, PurchaseItemRequest request) {
        log.info("Call to buy Power Item failed: {} for game {}", e.getMessage(),request.getGameId());

        if (e.status()==410){
            throw new GameOverException("Server abruptly closed the game");
        }

        if (request.getRetryCount() <= 0) {
            throw new ItemBuyException(request.getGameId(), request,
                    format("Retry limit reached. No more tries for Item Purchased request %s", request.toString()));
        }

        return retryPurchaseItemWithDelay(request);
    }


    public PurchasedItemDto savePurchasedItem (PurchasedItemDto purchasedItem){
        PurchasedItem purchasedItemEntity =purchasedItemMapper.toEntity(purchasedItem);
        Item item =itemRepository.save(itemMapper.toEntity(purchasedItem.getItemDto()));
        purchasedItemEntity.setItem(item);
        purchasedItemEntity.setTime(LocalDateTime.now());
        return purchasedItemMapper.toDto(purchasedItemRepository.save(purchasedItemEntity));
    }

    public List<ItemDto> saveAll(List<ItemDto> items) {
        return itemMapper.toDtos(itemRepository.saveAll(itemMapper.toEntities(items)));

    }

    private PurchaseItemResponse retryPurchaseItemWithDelay(PurchaseItemRequest request) {
        long retryDelay;
        switch (request.getRetryCount()) {
            case 1:
                retryDelay = 200L;
                break;
            case 2:
                retryDelay = 100L;
                break;
            case 3:
                retryDelay = 50L;
                break;
            default:
                throw new IllegalStateException("Retry count cant be lower or equal to zero");
        }

        log.info("Retry call to purchase Item {} . Retry count: {} ", request.getItemId(), request.getRetryCount());
        ThreadUtils.sleepQuietly(Duration.ofMillis(retryDelay));
        request.decreaseRetryCount();
        return purchaseItem(request);
    }

}
