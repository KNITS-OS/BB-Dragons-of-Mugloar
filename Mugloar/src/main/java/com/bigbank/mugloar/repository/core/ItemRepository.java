package com.bigbank.mugloar.repository.core;

import com.bigbank.mugloar.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Long> {
}
