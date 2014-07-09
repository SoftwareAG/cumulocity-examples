package com.cumulocity.agent.server.context.scope;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A locking mechanism based on keys, where a lock is created per given key.
 * @author Darek Kaczynski
 * 
 * @warning This class is copied from com.cumulocity.common.concurrent.KeyBasedLocksMap
 */
public class KeyBasedLocksMap {

    private final ConcurrentMap<Object, ReentrantLock> locks;
    
    public static Object createLockKey(Object...keyElements) {
        return new LockKey(keyElements);
    }
    
    public KeyBasedLocksMap() {
        this.locks = new ConcurrentHashMap<Object, ReentrantLock>();
    }

    /**
     * Acquires the lock constructing the key from given elements. <b>Each key element must implement {@link #hashCode()}
     * and {@link #equals(Object)} properly!</b>
     * @param keyElements the elements of the key.
     * @return the lock.
     * @see #lockForKey(Object)
     */
    public KeyBasedLock lockForKeyElements(Object...keyElements) {
        return lockForKey(createLockKey(keyElements));
    }
    
    /**
     * Acquires the lock for the given key. To acquire a lock for current thread means to successfully
     * {@link ConcurrentHashMap#putIfAbsent(Object, Object) putIfAbsent()} a {@link ReentrantLock lock} locked 
     * for current thread into {@link ConcurrentMap locks map}.
     * <p>Acquires the lock if it is not held by any thread and returns immediately, setting the lock hold count to one.
     * <p>If the lock is held by any thread then the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until the lock has been acquired, at which time the lock hold count is set to one.
     * <p>The algorithm of obtaining a lock is:<ol>
     * <li>One thread can obtain a lock only once at a time, so first we validate that if there already is a lock
     * for given key than it's not the current threads lock.
     * <li>Then current thread creates a new {@link ReentrantLock myLock} object and locks it.
     * <li>Then current thread tries to put it's {@link ReentrantLock myLock} to the locks map under given key.
     * <li>If {@link ConcurrentHashMap#putIfAbsent(Object, Object) putIfAbsent()} operation is successful, then the current
     * thread successfully acquired a lock for given key and the method returns.
     * <li>Otherwise {@link ConcurrentHashMap#putIfAbsent(Object, Object) putIfAbsent()} returns the currently held
     * {@link ReentrantLock otherLock} for given key by another thread, so current thread tries to acquire it -
     * using {@link ReentrantLock#lock() lock()} method it waits until it succeeds in acquiring the lock for itself.
     * <li>When current thread finally acquired the lock from different thread it unlocks previously created
     * {@link ReentrantLock myLock} object and uses the {@link ReentrantLock otherLock} in it's place and goes back
     * to step 2.
     * @param lockKey the key to acquire lock for.
     * @see Lock#lock()
     */
    public KeyBasedLock lockForKey(Object lockKey) {
        ReentrantLock myLock = locks.get(lockKey);
        if (myLock != null && myLock.isHeldByCurrentThread()) {
            myLock.lock();
            return new KeyBasedLock(lockKey, myLock);
        }
        
        myLock = new ReentrantLock();
        myLock.lock();
        ReentrantLock otherLock = null;
        do {
            otherLock = locks.putIfAbsent(lockKey, myLock);
            
            if (otherLock != null) {
                otherLock.lock();
                myLock.unlock();
                myLock = otherLock;
            }
        } while (otherLock != null);
        
        return new KeyBasedLock(lockKey, myLock);
    }
    
    void unlockKey(Object lockKey, ReentrantLock myLock) {
        if (myLock.getHoldCount() == 1) {
            locks.remove(lockKey);
        }
        myLock.unlock();
    }
    
    public class KeyBasedLock {
        
        private Object lockKey;
        private ReentrantLock lock;
        
        KeyBasedLock(Object lockKey, ReentrantLock lock) {
            this.lockKey = lockKey;
            this.lock = lock;
        }
        
        /**
         * Unlocks this the lock for this key.
         */
        public void unlock() {
            unlockKey(lockKey, lock);
        }
    }
    
    /**
     * A common lock key implementation. Proper {@link #hashCode()} and {@link #equals(Object)} implementations are
     * required for any key class to wotk correctly. 
     * @author Darek Kaczynski
     */
    private static class LockKey {
        
        private Object[] keyElements;
        
        public LockKey(Object...keyElements) {
            this.keyElements = keyElements;
        }
        
        @Override
        public int hashCode() {
            int hash = 0;
            if (keyElements != null) {
                for (Object element : keyElements) {
                    hash += (element == null ? 0 : element.hashCode());
                }
            }
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof LockKey)) {
                return false;
            }
            LockKey other = (LockKey) obj;
            if (keyElements == null) {
                return other.keyElements == null;
            } else if (other.keyElements == null) {
                return false;
            } else  if (keyElements.length != other.keyElements.length) {
                return false;
            }
            for (int i = 0; i < keyElements.length; i++) {
                if (keyElements[i] == null || other.keyElements[i] == null) {
                    if (keyElements != other.keyElements) {
                        return false;
                    }
                } else if (!keyElements[i].equals(other.keyElements[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}
