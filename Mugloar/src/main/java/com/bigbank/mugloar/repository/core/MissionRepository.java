package com.bigbank.mugloar.repository.core;

import com.bigbank.mugloar.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
   Optional<Mission> findOneByExternalIdAndGame_Id(String missionId, Long gameId);
}
