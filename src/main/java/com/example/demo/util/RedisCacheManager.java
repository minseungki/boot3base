package com.example.demo.util;

import com.example.demo.dto.common.RedisCacheKeyGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisCacheManager {

    public static final ObjectMapper REDIS_OBJECT_MAPPER;
    static {
        REDIS_OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
            .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        ;
    }

    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    @Primary
    public CacheManager cacheManager() {
        return getCacheManager(Duration.ofMinutes(30));
    }

    /**
     * 커스텀 keyGenerator 생성
     *
     * @return Redis에 캐싱할 Key
     */
    @Bean
    public KeyGenerator redisCacheKeyGenerator() {
        return new RedisCacheKeyGenerator();
    }

    @Bean(name = "cacheManager1seconds")
    public CacheManager cacheManager1seconds() {
        return this.getCacheManager(Duration.ofSeconds(1));
    }

    @Bean(name = "cacheManager5seconds")
    public CacheManager cacheManager5seconds() {
        return this.getCacheManager(Duration.ofSeconds(5));
    }

    @Bean(name = "cacheManager10seconds")
    public CacheManager cacheManager10seconds() {
        return this.getCacheManager(Duration.ofSeconds(10));
    }

    @Bean(name = "cacheManager30seconds")
    public CacheManager cacheManager30seconds() {
        return this.getCacheManager(Duration.ofSeconds(30));
    }

    @Bean(name = "cacheManager1minutes")
    public CacheManager cacheManager1minutes() {
        return this.getCacheManager(Duration.ofMinutes(1));
    }

    @Bean(name = "cacheManager5minutes")
    public CacheManager cacheManager5minutes() {
        return this.getCacheManager(Duration.ofMinutes(5));
    }

    @Bean(name = "cacheManager10minutes")
    public CacheManager cacheManager10minutes() {
        return this.getCacheManager(Duration.ofMinutes(10));
    }

    @Bean(name = "cacheManager30minutes")
    public CacheManager cacheManager30minutes() {
        return this.getCacheManager(Duration.ofMinutes(30));
    }

    @Bean(name = "cacheManager1Hour")
    public CacheManager cacheManager1Hour() {
        return this.getCacheManager(Duration.ofHours(1));
    }

    @Bean(name = "cacheManager5Hour")
    public CacheManager cacheManager5Hour() {
        return this.getCacheManager(Duration.ofHours(5));
    }

    @Bean(name = "cacheManager10Hour")
    public CacheManager cacheManager10Hour() {
        return this.getCacheManager(Duration.ofHours(10));
    }

    @Bean(name = "cacheManager30Hour")
    public CacheManager cacheManager30Hour() {
        return this.getCacheManager(Duration.ofHours(30));
    }

    @Bean(name = "cacheManager1Days")
    public CacheManager cacheManager1Days() {
        return this.getCacheManager(Duration.ofDays(1));
    }

    @Bean(name = "cacheManager5Days")
    public CacheManager cacheManager5Days() {
        return this.getCacheManager(Duration.ofDays(5));
    }

    @Bean(name = "cacheManager10Days")
    public CacheManager cacheManager10Days() {
        return this.getCacheManager(Duration.ofDays(10));
    }

    @Bean(name = "cacheManager30Days")
    public CacheManager cacheManager30Days() {
        return this.getCacheManager(Duration.ofDays(30));
    }

    /**
     * CacheManager 생성용 함수
     * @param duration <br>
     * - 10초 캐싱 : Duration.ofSeconds(10)<br>
     * - 10분 캐싱 : Duration.ofMinutes(10)<br>
     * - 10일 캐싱 : Duration.ofDays(10)<br>
     * @return
     */
    private CacheManager getCacheManager(Duration duration) {
        org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder builder = org.springframework.data.redis.cache.RedisCacheManager
            .RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory);

        // 값은 json 문자열로 넣는다. @class 필드로 클래스 정보가 들어간다.
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER)))
                .entryTtl(duration);

        builder.cacheDefaults(defaultConfig);
        return builder.build();
    }

}
