package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @desc 使用 ThreadLocal
 * @Author Chentian
 * @date 2021/4/10
 */
public class UseThreadLocal {

    private ThreadLocal<Integer> number = new ThreadLocal<>();

    private class MyRunnable implements Runnable{
        @Override
        public void run() {
            for (int i = 0 ; i< 4 ; i++) {
                Integer val = number.get();
                if (val == null) {
                    val = 100;
                    number.set(val);
                } else {
                    val++;
                    number.set(val);
                }
                System.out.println(Thread.currentThread().getName() + " get number value=" + number.get());
            }
            //使用完后，清理
            number.remove();
        }
    }

    public void exec(){
        for (int i = 0 ; i < 2 ; i++){
            Thread thread = new Thread(new MyRunnable());
            thread.start();
        }
    }
    public static void main(String[] args) {
        new UseThreadLocal().exec();
    }

}
