package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @desc 不使用 ThreadLocal
 * @Author Chentian
 * @date 2021/4/10
 */
public class NoUseThreadLocal {

    private Integer number;

    private class MyRunnable implements Runnable{
        @Override
        public void run() {
            for (int i = 0 ; i< 4 ; i++) {
                Integer val = number;
                if (val == null) {
                    number = 100;
                } else {
                    number ++;
                }
                System.out.println(Thread.currentThread().getName() + " get number value=" + number);
            }
        }
    }

    public void exec(){
        for (int i = 0 ; i < 2 ; i++){
            Thread thread = new Thread(new MyRunnable());
            thread.start();
        }
    }
    public static void main(String[] args) {
        new NoUseThreadLocal().exec();
    }

}
