package com.company.platform.infra.cache;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {
    void set(String key, Object value, Duration ttl);

    Optional<Object> get(String key);

    void delete(String key);

    boolean setIfAbsent(String key, Object value, Duration ttl);
}
