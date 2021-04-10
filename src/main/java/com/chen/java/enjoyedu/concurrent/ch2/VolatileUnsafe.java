package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @desc volatile 无法保证原子性
 * @Author Chentian
 * @date 2021/4/10
 */
public class VolatileUnsafe {

    private static volatile int age = 100;

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

    public static void main(String[] args) {
        for (int i = 0 ; i < 5 ; i++){
            Thread thread = new Thread(new AddRunnable());
            thread.start();
        }
    }
}
