package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @desc volatile 使用，一个线程写，多个线程读
 * @Author Chentian
 * @date 2021/4/10
 */
public class VolatileCase {

    //使用 volatile
    private static volatile int age = 100;

    //修改共享变量
    private static class AddRunnable implements Runnable{
        @Override
        public void run() {
            for (int i = 0 ; i < 10 ; i++) {
                try {
                    age++;
                    System.out.println(Thread.currentThread().getName()+" update age="+age+"--------------");
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    //读取共享变量
    private static class GetRunnable implements Runnable{
        @Override
        public void run() {
            try {
                for (int i = 0 ; i < 10; i++) {
                    System.out.println(Thread.currentThread().getName()+" time["+i+"] get age=" + age);
                    Thread.sleep(500);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Thread addThread = new Thread(new AddRunnable());
        addThread.start();

        for (int i = 0 ; i < 5 ; i++){
            Thread thread = new Thread(new GetRunnable());
            thread.start();
        }


    }

}
