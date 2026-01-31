package com.hmdp.utils;

public interface ILock {
    /**
     * 尝试获取锁
     * @param timeout 锁持有时间
     * @return true表示成狗 false表示失败
     */
    boolean tryLock(long timeout);

    /**
     * 释放锁
     */
    void unlock();

}
