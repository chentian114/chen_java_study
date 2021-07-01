package com.chen.java.enjoyedu.concurrent.ch5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: Chentian
 * @date: Created in 2021/6/30 6:08
 * @desc 使用ReentrantLock + Condition 实现等待通知
 */
public class ExpressCondOneLock {

    static final String CITY = "ShangHai";
    static final int KM_NUM = 100 ;

    private ReentrantLock lock = new ReentrantLock();
    //处理地点变化等待与通知
    private Condition siteCond = lock.newCondition();
    //处理运输里程数变化等待与通知
    private Condition kmCond = lock.newCondition();

    // 运输里程数
    private int km;
    // 地点
    private String site;

    public ExpressCondOneLock(int km, String site) {
        this.km = km;
        this.site = site;
    }

    /** 变化运输里程数，然后通知处于wait状态并需要处理运输里程数变化的线程进行业务处理 */
    public void changeKm(int km){
        lock.lock();
        try{
            this.km = km;
            //通知其他在锁上等待的线程
            kmCond.signalAll();
        }finally {
            lock.unlock();
        }
    }

    /**  变化地点，然后通知处于wait状态并需要处理地点变化的线程进行业务处理 */
    public void changeSite(String newSite){
        lock.lock();
        try{
            this.site = newSite;
            //通知其他在锁上等待的线程
            siteCond.signalAll();
        }finally {
            lock.unlock();
        }
    }

    /** 业务处理线程-当快递的里程数变化到一个指这阈值（大于100）时进行业务操作（更新数据库） */
    public void waitKm(){
        lock.lock();
        try {
            // 检查里程数变化是否达到阈值
            while (this.km < KM_NUM){
                try {
                    System.out.println("check km thread["+Thread.currentThread().getName()
                            +"] km = "+km);
                    //未达到阈值，等待，释放锁
                    kmCond.await();
                    //里程数变化，里程数变化线程被唤醒
                    System.out.println("check km thread["+Thread.currentThread().getName()
                            +"] is be notify!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("check km thread["+Thread.currentThread().getName()
                    +"] is finish! 更新数据库！km = "+km);
        }finally {
            lock.unlock();
        }
    }

    /** 业务处理线程-当快递变化地点到达目的地时通知用户 */
    public void waitSite(){
        lock.lock();
        try{
            //检查位置是否到达目的地
            while (!CITY.equals(this.site)){
                try {
                    System.out.println("check site thread["+Thread.currentThread().getName()
                            +"] site = "+site);
                    //未到达目的地，等待，释放锁
                    siteCond.await();
                    //位置变化，位置变化业务处理线程被唤醒
                    System.out.println("check site thread["+Thread.currentThread().getName()
                            +"] is be notify");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("check site thread["+Thread.currentThread().getName()
                    +"] is finish! 通知用户！ site = "+site);
        }finally {
            lock.unlock();
        }

    }

}
