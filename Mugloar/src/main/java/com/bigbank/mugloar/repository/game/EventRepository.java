package com.bigbank.mugloar.repository.game;

import com.bigbank.mugloar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findAllByGameExternalId(String gameId);
}
