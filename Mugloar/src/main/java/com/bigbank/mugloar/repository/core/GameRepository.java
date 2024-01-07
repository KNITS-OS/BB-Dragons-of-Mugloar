package com.bigbank.mugloar.repository.core;

import com.bigbank.mugloar.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
   Optional<Game> findOneByGameId(String gameId);
}
