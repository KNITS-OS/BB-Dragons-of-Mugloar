package com.bigbank.mugloar.proxy;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "mission-proxy", url = "${mugloar.mugloar-api-host}")
public interface MissionProxy {


    @GetMapping("/api/v2/{gameId}/messages")
    List<MissionDto> getGameAvailableMissions(@PathVariable("gameId") String gameId);
    @PostMapping("/api/v2/{gameId}/solve/{adId}")
    MissionResultDto takeMission(@PathVariable("gameId") String gameId, @PathVariable("adId") String missionId);


}
