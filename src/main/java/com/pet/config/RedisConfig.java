package com.pet.config;

import java.time.Duration;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// 1. 引入 Jackson 相關套件
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis 設定類別
 * 繼承 CachingConfigurerSupport 以自訂錯誤處理器
 */
@Slf4j
@Configuration
public class RedisConfig implements CachingConfigurer {

    /**
     * Redis 容錯處理器
     * 當 Redis 連線失敗時，自動 fallback 到資料庫查詢，不中斷業務邏輯
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("Redis GET 失敗，略過快取直接查詢資料庫: cache={}, key={}, error={}", 
                        cache.getName(), key, e.getMessage());
                // 不拋出例外，Spring 會自動 fallback 執行原本的方法
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("Redis PUT 失敗，資料未快取: cache={}, key={}, error={}", 
                        cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("Redis EVICT 失敗，快取可能未清除: cache={}, key={}, error={}", 
                        cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("Redis CLEAR 失敗: cache={}, error={}", cache.getName(), e.getMessage());
            }
        };
    }

    /**
     * 可在Redis Key中看見JSAON資料
     */
    private GenericJackson2JsonRedisSerializer getJsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 1. 註冊 JavaTimeModule (解決 LocalDateTime 序列化錯誤)
        mapper.registerModule(new JavaTimeModule());
        // (選用) 讓時間變成好看的 ISO-8601 字串 (ex: "2026-01-20T12:00:00")，而不是 timestamp 數字
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 2. 啟用 DefaultTyping (這是 GenericJackson2JsonRedisSerializer 的核心，讓它知道存的是哪個 Class)
        // 如果沒加這行，Redis 取出來的資料會變成 LinkedHashMap，無法轉回 ServiceItem
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance, 
                ObjectMapper.DefaultTyping.NON_FINAL, 
                JsonTypeInfo.As.PROPERTY
        );

        return new GenericJackson2JsonRedisSerializer(mapper);
    }
    
    //Redis工具包
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 使用自訂的序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = getJsonSerializer();
        
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
    
    //Redis自動快取
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // 使用自訂的序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = getJsonSerializer();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) //設定快取資料存活時間為1小時
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) 
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)) 
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}