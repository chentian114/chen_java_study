package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @author: Chentian
 * @date: Created in 2021/4/14 6:24
 * @desc 对比notify() 和 notifyAll()
 */
public class TestNotifyAndNotifyAll {
    private static int initKM = 10;
    private static String initLoc = "Beijing";

    public static void main(String[] args) throws InterruptedException {
        Express express = new Express(initKM,initLoc);

        //等待距离变化线程
        Thread waitChangKmThread = new Thread(){
            @Override
            public void run() {
                synchronized (express){
                    while (express.getKm() > 0){
                        try {
                            express.wait();
                            System.out.println("Check KM thread["+Thread.currentThread().getName()
                                    +"] is be notified");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("the kim is "+express.getLocation()+",I will change location");
                    //距离改变，修改位置
                    express.changeLocation("Shanghai");
                    express.notifyAll();
                }
            }
        };

        //等待位置变化线程
        Thread waitChangeLocThread = new Thread(){
            @Override
            public void run() {
                synchronized (express){
                    while (initLoc.equals(express.getLocation())){
                        try {
                            express.wait();
                            System.out.println("Check Location thread["+Thread.currentThread().getName()
                                    +"] is be notified");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("the location is "+express.getLocation()+",I will call user");
            }
        };

        waitChangeLocThread.start();
        waitChangKmThread.start();

        System.out.println("快递开始运输。。。。");
        Thread.sleep(2000);
        synchronized (express) {
            //修改位置
            express.changeKm(0);
            //对象express上有多个线程等待通知，使用notify()，只会唤醒其中的一个线程，造成信号丢失，可能无法唤醒真正需要唤醒的线程
//            express.notify();

            //使用 notifyAll()，会唤醒所有等待线程，不会造成信号丢失
            express.notifyAll();
        }
    }


    //快递类
    static class Express{
        private int km;
        private String location;

        public Express(int km, String location) {
            this.km = km;
            this.location = location;
        }

        public synchronized void changeKm(int km) {
            this.km = km;
        }

        public synchronized void changeLocation(String location) {
            this.location = location;
        }

        public int getKm() {
            return km;
        }

        public String getLocation() {
            return location;
        }
    }


}
