package io.lithium.pokerstore.lock;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * Read-write named lock for service layer.
 * Use for pessimistic lock in general cases(e.g. possible pessimistic DB lock solution)
 */
public interface StripeLock {

    /**
     * Perform write lock and return instance by requested name.
     *
     * @param name lock name
     * @return write lock
     */
    ReadWriteLock acquireWrite(final LockName name);

    /**
     * Perform read lock and return instance by requested name.
     *
     * @param name lock name
     * @return read lock
     */
    ReadWriteLock acquireRead(final LockName name);
}
