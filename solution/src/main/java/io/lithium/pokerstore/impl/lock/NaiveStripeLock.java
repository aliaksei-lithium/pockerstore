package io.lithium.pokerstore.impl.lock;

import io.lithium.pokerstore.lock.LockName;
import io.lithium.pokerstore.lock.StripeLock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Striped lock implementation with ReadWriteLock with ConcurrentHashMap store.
 *
 * Naive(simple and made for current tasks only) implementation for business needs.
 */
public final class NaiveStripeLock implements StripeLock {

    private static final Map<LockName, ReadWriteLock> locks = new ConcurrentHashMap<>();

    public static final NaiveStripeLock INSTANCE = new NaiveStripeLock();

    private NaiveStripeLock() {
        locks.put(LockName.CUSTOMER, new ReentrantReadWriteLock());
        locks.put(LockName.PRODUCT, new ReentrantReadWriteLock());
    }

    @Override
    public ReadWriteLock acquireWrite(final LockName name) {
        ReadWriteLock lock = getLock(name);
        lock.writeLock().lock();
        return lock;
    }

    @Override
    public ReadWriteLock acquireRead(LockName name) {
        ReadWriteLock lock = getLock(name);
        lock.readLock().lock();
        return lock;
    }

    private ReadWriteLock getLock(final LockName name) {
        ReadWriteLock lock = locks.get(name);

        if (lock == null) {
            lock = new ReentrantReadWriteLock();
            locks.put(name, lock);
        }

        return lock;
    }
}
