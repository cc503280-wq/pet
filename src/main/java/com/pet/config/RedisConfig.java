package com.pet.config;

import java.time.Duration;

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

@Configuration
public class RedisConfig {

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