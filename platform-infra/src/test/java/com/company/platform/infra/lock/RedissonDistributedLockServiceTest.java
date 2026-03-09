package com.company.platform.infra.lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedissonDistributedLockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @Test
    void shouldTryLockAndUnlock() throws Exception {
        when(redissonClient.getLock("lock:key")).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        RedissonDistributedLockService service = new RedissonDistributedLockService(redissonClient);
        boolean locked = service.tryLock("lock:key", Duration.ofSeconds(1), Duration.ofSeconds(3));
        service.unlock("lock:key");

        assertTrue(locked);
        verify(lock).unlock();
    }
}
