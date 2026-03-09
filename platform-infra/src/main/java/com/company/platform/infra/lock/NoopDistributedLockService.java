package com.company.platform.infra.lock;

import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(DistributedLockService.class)
public class NoopDistributedLockService implements DistributedLockService {

    @Override
    public boolean tryLock(String key, Duration waitTime, Duration leaseTime) {
        return false;
    }

    @Override
    public void unlock(String key) {
    }
}
