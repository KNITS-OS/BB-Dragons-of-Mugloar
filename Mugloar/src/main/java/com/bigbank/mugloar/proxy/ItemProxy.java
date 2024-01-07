package com.bigbank.mugloar.proxy;

import com.bigbank.mugloar.dto.api.external.domugloar.ItemResponse;
import com.bigbank.mugloar.dto.api.external.domugloar.PurchaseItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "item-proxy", url = "${mugloar.mugloar-api-host}")
public interface ItemProxy {

    @GetMapping("/api/v2/{gameId}/shop")
    List<ItemResponse> listItems(@PathVariable("gameId") String gameId);

    @PostMapping("/api/v2/{gameId}/shop/buy/{itemId}")
    PurchaseItemResponse purchaseItem(@PathVariable("gameId") String gameId, @PathVariable("itemId") String itemId);
}
