package com.example.demo.util;

import com.example.demo.dto.common.RedisAuthenticationVo;
import com.example.demo.dto.common.enumeration.RedisCd;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisUtil {

    private final int REDIS_SCAN_COUNT = 5000;
    private final int REDIS_SCAN_DELETE_COUNT = 3000;

    @Value("${jwt.token-validity-in-time.access}")
    private long JWT_ACCESS_EXPIRATION;

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void set(String key, Object value, Duration timeout) {
        try {
            ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
            stringValueOperations.set(key, objectMapper.writeValueAsString(value), timeout);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setOfMillis(String key, Object value, long timeout) {
        set(key, value, Duration.ofMillis(timeout));
    }
    public void setOfSeconds(String key, Object value, long timeout) {
        set(key, value, Duration.ofSeconds(timeout));
    }

    public void setOfMinutes(String key, Object value, long timeout) {
        set(key, value, Duration.ofMinutes(timeout));
    }

    public void setOfHours(String key, Object value, long timeout) {
        set(key, value, Duration.ofHours(timeout));
    }

    public void setOfDays(String key, Object value, long timeout) {
        set(key, value, Duration.ofDays(timeout));
    }

    public <T> T get(String key, Class<T> classType) {
        String redisValue = stringRedisTemplate.opsForValue().get(key);
        if (null != redisValue) {
            try {
                return objectMapper.readValue(redisValue, classType);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return null;
        }
    }

    public boolean remove(String delKey) {
        final RedisConnection connection = Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()).getConnection();
        try {
            final Set result = new HashSet<String>();
            final ScanOptions options = ScanOptions.scanOptions().match(delKey).count(REDIS_SCAN_COUNT).build();

            Cursor<byte[]> cursor = connection.scan(options);
            long deleteCount = 0;

            while (cursor.hasNext()) {
                result.add((new String(cursor.next())));
                deleteCount++;
                if (result.size() > REDIS_SCAN_DELETE_COUNT) {
                    stringRedisTemplate.delete(result);
                    result.clear();
                }
            }

            if (result.size() > 0) {
                deleteCount = deleteCount + result.size();
                stringRedisTemplate.delete(result);
            }

            log.error("Redis key delete : [" + delKey + "], deleteCount : [" + deleteCount + "]");

            connection.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            connection.close();
        }
        return true;
    }

    public void setRedisUserInfo(RedisAuthenticationVo redisAuthenticationVo) {
        String redisKey = String.join(":", RedisCd.MEM001.name(), redisAuthenticationVo.getUserId());
        long baseTimeout = JWT_ACCESS_EXPIRATION * 1000 * 60; // 분단위
        remove(redisKey);
        setOfMinutes(redisKey, redisAuthenticationVo, baseTimeout);
    }

    public RedisAuthenticationVo getRedisUserInfo(String id) {
        String redisKey = String.join(":", RedisCd.MEM001.name(), id);
        return get(redisKey, RedisAuthenticationVo.class);
    }

}
