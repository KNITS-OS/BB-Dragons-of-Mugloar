package com.bigbank.mugloar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mugloar", ignoreUnknownFields = false)
@Data
public class ApplicationProperties {

    private boolean saveExecution;
    private String mugloarApiHost;
    private boolean autoStartGame;

    private Cache cache;
    private Strategy strategy;
    private Async async;
    private Throttling throttling;

    @Data
    public static class Cache {
        private int initialCapacity;
        private int maximumSize;
        private int expireAfterWriteInSeconds;
        private int expireAfterLastAccessInSeconds;
    }

    @Data
    public static class Strategy {
        private int goldReserveHotPot;
        private int livesMinSafeLevel;
        private int missionNotFoundThreshold;
        private int livesAcceptSafeLimit;
        private int livesAcceptEasyLimit;
        private int livesAcceptRiskyLimit;
    }
    @Data
    public static class Async {
        private boolean asyncExecution;
        private String asyncCallbackUrl;
        private int asyncExecutorPoolSize;
    }

    @Data
    public static class Throttling {
        private long apiThrottlingDelay ;
        private long apiThrottlingOnExceptionDelay ;
    }

}
