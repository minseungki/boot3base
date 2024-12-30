package com.example.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisUtil {

    private final int REDIS_SCAN_COUNT = 5000;
    private final int REDIS_SCAN_DELETE_COUNT = 3000;

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void setValue(String key, Object redisData, Duration timeout) {
        try {
            ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
            stringValueOperations.set(key, objectMapper.writeValueAsString(redisData), timeout);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T> T getValue(String key, Class<T> classType) {
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

    public boolean deleteKey(String delKey) {
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
            ex.printStackTrace();
            return false;
        } finally {
            connection.close();
        }
        return true;
    }

    public Long getCountByKeyPattern(String pattern) {
        final RedisConnection connection = Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()).getConnection();
        try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(100).build())) {
            long count = 0;
            while (cursor.hasNext()) {
                cursor.next();
                count++;
            }
            return count;
        } catch (Exception ex) {
            throw ex;
        } finally {
            connection.close();
        }
    }

    public void setRedisValueSetTimeoutOfSeconds(String key, Object redisData, long timeout) {
        try {
            ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();

            stringValueOperations.set(key, objectMapper.writeValueAsString(redisData), Duration.ofSeconds(timeout));
        } catch (JsonProcessingException e) {
            log.error("error : {}", e);
            throw new RuntimeException(e);
        }
    }

    public long getListLowerTimeoutOfSec(List<LocalDateTime> list) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime minDate = Collections.min(list);

        String formatDate = minDate.format(format);
        if (formatDate.contains("99")) {
            // 노출 종료일자에 99가 포함되면 1일을 초단위로 리턴
            return 60 * 60 * 24;
        } else {
            // 노출 종료일자에 99가 포함되지 않으면 현재 시간과 차이 초단위로 리턴
            return Duration.between(LocalDateTime.now(), minDate).getSeconds();
        }
    }

    public String getRedisKeyByDto(Object o) {
        String redisKey = RestUtil.classToJsonStr(o);
        if (StringUtils.hasText(redisKey)) {
            redisKey = redisKey
                    .replace(":", "(")
                    .replace(",", ")")
                    .replace("}", ")}");
        }
        return redisKey;
    }

}
