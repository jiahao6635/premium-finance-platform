package com.company.platform.rbac.security;

import com.company.platform.infra.cache.CacheService;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class TokenBlacklistService {
    private static final String KEY_PREFIX = "auth:blacklist:";

    private final CacheService cacheService;

    public TokenBlacklistService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public void blacklist(String tokenId, long ttlSeconds) {
        if (tokenId == null || tokenId.isBlank() || ttlSeconds <= 0) {
            return;
        }
        cacheService.set(KEY_PREFIX + tokenId, Boolean.TRUE, Duration.ofSeconds(ttlSeconds));
    }

    public boolean isBlacklisted(String tokenId) {
        return cacheService.get(KEY_PREFIX + tokenId).isPresent();
    }
}
