package com.bigbank.mugloar.dto.domain.core;

import com.bigbank.mugloar.model.MissionOutcomeType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"adId"})
public class MissionDto {

    private String adId;
    private String gameExternalId;
    private String message;
    private int reward;
    private int expiresIn;
    private String encrypted;
    private String probability;
    private Integer riskWeightedReward;
    private MissionOutcomeType outcome;

    @Builder.Default
    private int retryCount=3;

    private GameDto game;
    public void decreaseRetryCount(){
        this.retryCount--;
    }



}
