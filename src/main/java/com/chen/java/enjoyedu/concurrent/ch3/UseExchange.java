package com.chen.java.enjoyedu.concurrent.ch3;

import java.util.concurrent.Exchanger;

/**
 * @desc 使用Exchange
 * @Author Chentian
 * @date 2021/4/17
 */
public class UseExchange {

    public static void main(String[] args) throws InterruptedException {

        Exchanger<String> exchanger = new Exchanger<>();

        Thread thread1 = new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+" use exchange...");
                try{

                    Thread.sleep(1000);
                    String value = exchanger.exchange(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName()+" use exchange get value="+value);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread1.start();

        Thread thread2 = new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+" use exchange...");
                try{
                    Thread.sleep(2000);
                    String value = exchanger.exchange(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName()+" use exchange get value="+value);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread2.start();


        Thread.sleep(2000);
    }
}
