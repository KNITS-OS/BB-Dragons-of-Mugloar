package com.bigbank.mugloar.config;

import com.bigbank.mugloar.util.Consts;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableCaching
public class ApplicationConfiguration {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(Consts.GAME_CACHE,
                Consts.ITEMS_AVAILABLE_CACHE, Consts.ITEMS_PRIORITIZED_CACHE);
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(applicationProperties.getAsync().getAsyncExecutorPoolSize());
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(applicationProperties.getCache().getInitialCapacity())
                .maximumSize(applicationProperties.getCache().getMaximumSize())
                .expireAfterAccess(Duration.ofMinutes(applicationProperties.getCache().getExpireAfterLastAccessInSeconds()))
                .expireAfterWrite(Duration.ofMinutes(applicationProperties.getCache().getExpireAfterWriteInSeconds()));
    }


}
