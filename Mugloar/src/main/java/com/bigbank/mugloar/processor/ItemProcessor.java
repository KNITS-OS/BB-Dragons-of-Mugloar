package com.bigbank.mugloar.processor;

import com.bigbank.mugloar.dto.domain.core.GameDto;

public interface ItemProcessor {

    void upgradeGameWithPowerItems(GameDto currentGame);


}
