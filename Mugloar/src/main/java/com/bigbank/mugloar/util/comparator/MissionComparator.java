package com.bigbank.mugloar.util.comparator;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import lombok.extern.slf4j.Slf4j;
import java.util.Comparator;

@Slf4j
public class MissionComparator implements Comparator<MissionDto> {
    @Override
    public int compare(MissionDto m1, MissionDto m2) {
        return m2.getRiskWeightedReward().compareTo(m1.getRiskWeightedReward());
    }
}
