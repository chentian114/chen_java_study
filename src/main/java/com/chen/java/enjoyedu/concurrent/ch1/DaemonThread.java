package com.chen.java.enjoyedu.concurrent.ch1;

/**
 * @author: Chentian
 * @date: Created in 2021/4/6 23:31
 * @desc 守护线程使用
 */
public class DaemonThread {

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    while (!this.isInterrupted()){
                        System.out.println("hello thread!");
                    }
                    System.out.println(Thread.currentThread().getName()
                            + " interrupt flag is " + isInterrupted());
                }finally {
                    //守护线程中finally不一定起作用
                    System.out.println("finally....");
                }
            }
        };

        System.out.println("start...");
        //设置为守护线程，主线程结束后，守护线程也会结束
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(5);
    }


}
