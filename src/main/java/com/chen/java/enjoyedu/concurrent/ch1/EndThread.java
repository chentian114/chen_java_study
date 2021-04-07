package com.chen.java.enjoyedu.concurrent.ch1;

/**
 * @author: Chentian
 * @date: Created in 2021/4/6 22:55
 * @desc 使用 interrupt() 中断线程
 */
public class EndThread {

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(){
            @Override
            public void run() {
                while (!this.isInterrupted()){
                    System.out.println("hello run!");
                }
            }
        };

        System.out.println("start...");
        thread.start();
        Thread.sleep(10);
        thread.interrupt();
        System.out.println("end...");

    }
}
