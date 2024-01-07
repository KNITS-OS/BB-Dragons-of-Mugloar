package com.bigbank.mugloar.processor;

import com.bigbank.mugloar.dto.domain.game.MissionBatchDto;

public interface MissionProcessor {
    MissionBatchDto processCurrentMissionBatch(MissionBatchDto missionBatch);
}
