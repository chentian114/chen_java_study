package com.chen.java.enjoyedu.concurrent.ch1;

/**
 * @author: Chentian
 * @date: Created in 2021/4/6 23:13
 * @desc
 */
public class EndRunnable {

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()){
                    System.out.println("hello run");
                }
            }
        };

        System.out.println("start...");
        Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(20);
        thread.interrupt();
        System.out.println("end...");
    }
}
