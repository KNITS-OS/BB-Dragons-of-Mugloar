package com.bigbank.mugloar.exception;

import com.bigbank.mugloar.dto.api.external.domugloar.PurchaseItemRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ItemBuyException extends RuntimeException{

    private String gameId;
    private PurchaseItemRequest purchaseItemRequest;

    public ItemBuyException(String gameId, PurchaseItemRequest purchaseItemRequest, String message ) {
        super(message);
        this.gameId=gameId;
        this.purchaseItemRequest=purchaseItemRequest;
    }
}
