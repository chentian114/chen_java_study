package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @author: Chentian
 * @date: Created in 2021/4/13 8:21
 * @desc 测试Sleep对锁的影响
 */
public class SleepLock {
    //使用对象作为锁
    private static Object lock = new Object();

    public static void main(String[] args) {

        Thread sleepThread = new Thread(){
            @Override
            public void run() {
                synchronized (lock){
                    System.out.println(Thread.currentThread().getName()+" get lock, sleep....");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+" run finish!");
                }
            }
        };

        Thread noSleepThread = new Thread(){
            @Override
            public void run() {
                synchronized (lock){
                    System.out.println(Thread.currentThread().getName()+" get lock, no sleep....");
                    System.out.println(Thread.currentThread().getName()+" run finish!");
                }
            }
        };

//        sleepThread.start();
        noSleepThread.start();
        sleepThread.start();


    }
}
