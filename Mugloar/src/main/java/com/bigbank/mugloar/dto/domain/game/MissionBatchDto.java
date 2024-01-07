package com.bigbank.mugloar.dto.domain.game;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Queue;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class MissionBatchDto {

    private String gameId;

    @Builder.Default
    private int missionExecutionCounter=0;

    @Builder.Default
    private int missionNotFoundCounter=0;

    @ToString.Exclude
    private Set<MissionDto> availableMissions;

    @ToString.Exclude
    private Queue<MissionDto> safeMissions;

    @ToString.Exclude
    private Queue<MissionDto> easyMissions;

    @ToString.Exclude
    private Queue<MissionDto> riskyMissions;

    @ToString.Exclude
    private List<MissionDto> executedMissions;

    @ToString.Exclude
    private List<MissionResultDto> executedMissionResults;

    @ToString.Exclude
    private List<MissionDto> skippedMissions;

    @ToString.Exclude
    private List<MissionResultDto> skippedMissionsResults;

    @ToString.Exclude
    private List<MissionDto> failedMissions;

    @ToString.Exclude
    private List<MissionResultDto> failedMissionsResults;

    public void increaseMissionExecutionCounter(){
        missionExecutionCounter++;
    }

    public void increaseMissionNotFoundCounter(){
        missionNotFoundCounter++;
    }

    public boolean isMissionAvailable (MissionDto mission){
        if(!getAvailableMissions().contains(mission)){
            log.warn("Mission {} candidate is missing in batch!",mission.getAdId());
            return false;
        }
        log.info("Mission {} Expires {} batch Ex Counter: {}",mission.getAdId(),mission.getExpiresIn(),getMissionExecutionCounter());
        return (mission.getExpiresIn()>getMissionExecutionCounter());
    }

}
