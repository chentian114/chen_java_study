package com.chen.java.enjoyedu.concurrent.ch1;

/**
 * @author: Chentian
 * @date: Created in 2021/4/6 23:08
 * @desc 线程执行方法中会抛出 InterruptedException 异常的方法，使用 interrupt() 中断线程
 */
public class HasInterruptException {

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(){
            @Override
            public void run() {

                while (!this.isInterrupted()){
                    System.out.println("hello thread");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        System.out.println(Thread.currentThread().getName()
                                +" in InterruptedException interrupt flag is "
                                +isInterrupted());
                        //资源释放，
                        // 如果线程执行方法中会抛出 InterruptedException 异常的方法，使用try...catch 捕获中断异常，
                        // 在外部调用中断线程时，会被catch捕获，且线程的中断标志位为重置为false，
                        // 需要在catch中重新调用一个 interrupt() 中断方法。否则线程不会中断
                        this.interrupt();
                        e.printStackTrace();
                    }
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
