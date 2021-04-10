package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @desc 演示类锁和对象锁
 * @Author Chentian
 * @date 2021/4/10
 */
public class SyncClzAndInst {

    public static void main(String[] args) throws InterruptedException {

        //场景一：两个线程使用两个不同的对象锁，两个线程同时执行
        SyncClzAndInst inst1 = new SyncClzAndInst();
        Thread thread0 = new SyncInst(inst1);
        SyncClzAndInst inst2 = new SyncClzAndInst();
        Thread thread1 = new SyncInst(inst2);
        thread0.start();
        thread1.start();

        Thread.sleep(500);
        System.out.println("====================");

        //场景二：两个线程使用同一个对象锁，两个线程串行执行
        SyncClzAndInst inst3 = new SyncClzAndInst();
        Thread thread2 = new SyncInst(inst3);
        Thread thread3 = new SyncInst(inst3);
        thread2.start();
        thread3.start();

        Thread.sleep(500);
        System.out.println("====================");

        //场景三：两个线程使用一个类锁和一个对象锁，两个线程同时执行
        SyncClzAndInst inst4 = new SyncClzAndInst();
        Thread thread4 = new SyncInst(inst4);
        SyncClass thread5 = new SyncClass();
        thread4.start();
        thread5.start();

        Thread.sleep(500);
        System.out.println("====================");

        //场景四：两个线程使用同一个类锁，两个线程串行执行
        SyncClass thread6 = new SyncClass();
        SyncClass thread7 = new SyncClass();
        thread6.start();
        thread7.start();

    }

    //使用类锁的方法
    private synchronized static void syncClass() {
        try {
            Thread.sleep(5);
            System.out.println(Thread.currentThread().getName()+" syncClass is running...");
            Thread.sleep(5);
            System.out.println(Thread.currentThread().getName()+" syncClass is end!");
            System.out.println("------------------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //使用对象锁的方法
    private synchronized void syncInstance() {
        try {
            Thread.sleep(5);
            System.out.println(Thread.currentThread().getName()+" syncInstance is running...");
            Thread.sleep(5);
            System.out.println(Thread.currentThread().getName()+" syncInstance is end!");
            System.out.println("------------------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //使用类锁的线程
    private static class SyncClass extends Thread{
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" hello syncClass...");
            //调用类锁方法
            syncClass();
        }
    }

    //使用对象锁的线程
    private static class SyncInst extends Thread{
        private SyncClzAndInst syncClzAndInst;
        public SyncInst(SyncClzAndInst syncClzAndInst){
            this.syncClzAndInst = syncClzAndInst;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" hello syncInst...");
            syncClzAndInst.syncInstance();
        }
    }



}
