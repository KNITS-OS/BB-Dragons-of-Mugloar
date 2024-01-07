package com.bigbank.mugloar.repository.core;

import com.bigbank.mugloar.model.PurchasedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasedItemRepository extends JpaRepository<PurchasedItem, Long> {
}
