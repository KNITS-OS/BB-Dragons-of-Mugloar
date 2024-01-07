package com.bigbank.mugloar.proxy;

import com.bigbank.mugloar.dto.api.external.domugloar.StartGameResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "game-proxy", url = "${mugloar.mugloar-api-host}")
public interface GameProxy {
    @PostMapping("/api/v2/game/start")
    StartGameResponse startGame ();
}
