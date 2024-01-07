package com.bigbank.mugloar.service.core;

import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import com.bigbank.mugloar.mappers.MissionResultMapper;
import com.bigbank.mugloar.model.MissionResult;
import com.bigbank.mugloar.repository.core.MissionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MissionResultService {

    private final MissionResultMapper missionResultMapper;
    private final MissionResultRepository missionResultRepository;
    public List<MissionResultDto> saveAll(List<MissionResultDto> missionResults){
        List<MissionResult> results =missionResultMapper.toEntities(missionResults);
        return missionResultMapper.toDtos(missionResultRepository.saveAll(results));
    }

}
