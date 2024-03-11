package com.walmart.aex.sp.config;

import io.sixhours.memcached.cache.MemcachedCacheManager;
import io.sixhours.memcached.cache.MemcachedCacheProperties;
import io.sixhours.memcached.cache.XMemcachedCacheManagerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties({MemcachedCacheProperties.class})
public class CacheConfig {
    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("appMessages");
    }

    @Bean
    @Profile("!test")
    @ConditionalOnMissingBean(
            value = {MemcachedCacheManager.class},
            search = SearchStrategy.CURRENT
    )
    public MemcachedCacheManager memCacheManager(MemcachedCacheProperties properties) throws IOException {
        return (new XMemcachedCacheManagerFactory(properties)).create();
    }

}
