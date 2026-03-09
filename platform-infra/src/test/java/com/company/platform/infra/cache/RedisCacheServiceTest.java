package com.company.platform.infra.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Test
    void shouldDelegateSetGetAndDelete() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("k")).thenReturn("v");

        RedisCacheService cacheService = new RedisCacheService(redisTemplate);
        cacheService.set("k", "v", Duration.ofSeconds(5));
        cacheService.get("k");
        cacheService.delete("k");

        verify(valueOperations).set("k", "v", Duration.ofSeconds(5));
        verify(valueOperations).get("k");
        verify(redisTemplate).delete("k");
    }
}
