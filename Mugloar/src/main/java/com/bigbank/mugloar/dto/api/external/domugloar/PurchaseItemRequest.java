package com.bigbank.mugloar.dto.api.external.domugloar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseItemRequest {
    String gameId;
    String itemId;
    @Builder.Default
    int retryCount = 3;

    public void decreaseRetryCount() {
        retryCount--;
    }
}
