package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @desc 测试 notify() 和 notifyAll()
 * @Author Chentian
 * @date 2021/4/10
 */
public class TestWaitAndNotify {

    private static boolean state = false;

    public static void main(String[] args) {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                synchronized (TestWaitAndNotify.class){
                    while (!state) {
                        System.out.println(Thread.currentThread().getName()+" state="+state+" hello Object wait()!");
                        try {
                            TestWaitAndNotify.class.wait(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(Thread.currentThread().getName()+" state="+state+" finish wait!");
                }
            }
        };
        thread1.start();

        Thread thread2 = new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+" hello Object notify()!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (TestWaitAndNotify.class){
                    state = true;
                    System.out.println(Thread.currentThread().getName()+" run notify!");
                    TestWaitAndNotify.class.notify();
                }
            }
        };
        thread2.start();
    }
}
