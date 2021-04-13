package com.chen.java.enjoyedu.concurrent.ch2;

import java.util.Random;

/**
 * @author: Chentian
 * @date: Created in 2021/4/12 23:45
 * @desc 使用 wait/notify 实现 子弹上膛和子弹射击的生产者消费者
 */
public class GunWaitAndNotify {

    //最大容量
    private static final int MAX_SIZE = 20;

    //枪的弹匣
    private int gunPool;

    //上膛
    private synchronized void load(){
        if(gunPool == MAX_SIZE){
            try {
                System.out.println(Thread.currentThread().getName()+" 弹匣已满，无法装弹！");
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            gunPool++;
            System.out.println(Thread.currentThread().getName()+" 子弹上膛： gunPool="+gunPool);
            this.notifyAll();
        }
    }

    private synchronized void shot(){
        if(gunPool == 0){
            System.out.println(Thread.currentThread().getName()+" 弹匣已空，无法射击！");
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            gunPool--;
            System.out.println(Thread.currentThread().getName()+" 子弹射击： gunPool="+gunPool);
            this.notifyAll();
        }
    }

    //生产者任务，执行子弹上膛
    private static class ProducerRunnable implements Runnable{
        private GunWaitAndNotify gun;
        private ProducerRunnable(GunWaitAndNotify gun){
            this.gun = gun;
        }
        @Override
        public void run() {
            Random random = new Random();
            for (int i = 0 ; i < 50 ; i++){
                gun.load();
                try {
                    Thread.sleep(random.nextInt(50));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //消费者任务，执行子弹射击
    private static class ConsumerRunnable implements Runnable{
        private GunWaitAndNotify gun;
        private ConsumerRunnable(GunWaitAndNotify gun){
            this.gun = gun;
        }
        @Override
        public void run() {
            Random random = new Random();
            for (int i = 0 ; i < 50 ; i++) {
                gun.shot();
                try {
                    Thread.sleep(random.nextInt(100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("start...");
        GunWaitAndNotify gun = new GunWaitAndNotify();
        for (int i = 0 ; i < 6 ; i++) {
            Thread producerThread = new Thread(new ProducerRunnable(gun));
            producerThread.start();
        }
        for (int i = 0 ; i < 6 ; i++) {
            Thread consumerThread = new Thread(new ConsumerRunnable(gun));
            consumerThread.start();
        }

    }
}
