package com.company.platform.infra.lock;

import java.time.Duration;

public interface DistributedLockService {
    boolean tryLock(String key, Duration waitTime, Duration leaseTime);

    void unlock(String key);
}
