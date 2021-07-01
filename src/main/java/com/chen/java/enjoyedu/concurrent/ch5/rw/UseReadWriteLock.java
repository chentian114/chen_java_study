package com.chen.java.enjoyedu.concurrent.ch5.rw;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: Chentian
 * @date: Created in 2021/6/30 7:19
 * @desc 使用 ReadWriteLock
 */
public class UseReadWriteLock implements GoodsService{
    private GoodsInfo goodsInfo;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock getLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public UseReadWriteLock(GoodsInfo goodsInfo){
        this.goodsInfo = goodsInfo;
    }

    @Override
    public GoodsInfo getNum() {
        getLock.lock();
        try{
            try {
                TimeUnit.MICROSECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return goodsInfo;
        }finally {
            getLock.unlock();
        }
    }

    @Override
    public void changeNum(int number) {
        writeLock.lock();
        try{
            try {
                TimeUnit.MICROSECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            goodsInfo.changeNumber(number);
        }finally {
            writeLock.unlock();
        }
    }
}
