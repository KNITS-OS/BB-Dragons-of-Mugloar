package com.bigbank.mugloar.processor;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.game.MissionBatchDto;

public interface MissionEvaluator {

    MissionDto selectNextMissionFromBatch(MissionBatchDto missionBatchDto);
}
