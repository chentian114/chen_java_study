package com.chen.java.enjoyedu.concurrent.ch3;

import java.util.concurrent.CountDownLatch;

/**
 * @desc CountDownLatch使用示例
 * @Author Chentian
 * @date 2021/4/17
 * 需求：共5个初始化子线程，6个闭锁扣除点，扣除完毕后，主线程和业务线程才能继续执行；
 */
public class UseCountDownLatch {


    public static void main(String[] args) {

        //创建闭锁
        CountDownLatch countDownLatch = new CountDownLatch(6);

        //初始线程
        for (int i = 0 ; i < 4 ; i++){
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName()+" init do something...");
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        countDownLatch.countDown();
                    }

                }
            };
            thread.setName("initThread-"+i);
            thread.start();
        }

        Thread initThread = new Thread(){
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName()+" init do part1 something...");
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    countDownLatch.countDown();
                }
                try{
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName()+" init do part2 something...");
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    countDownLatch.countDown();
                }
            }
        };
        initThread.setName("initThread-"+4);
        initThread.start();

        Thread busiThread = new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+" wait init finish!");
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+" start do business something!");
            }
        };

        busiThread.start();


        try {
            //等待业务线程执行完成
            busiThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
