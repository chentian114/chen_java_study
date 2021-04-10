package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @desc 错误使用ThreadLocal导致线程不安全
 * @Author Chentian
 * @date 2021/4/10
 */
public class ThreadLocalUnsafe {

    //共享变量的值
    private static Number number = new Number(100);

    //共享变量
    private static ThreadLocal<Number> threadLocalNumber = new ThreadLocal<>();


    public static void main(String[] args) {
        for (int i = 0 ; i < 5 ; i++){
            Thread thread = new Thread(){
                @Override
                public void run() {
                    number.incr();
                    threadLocalNumber.set(number);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+" get number="+threadLocalNumber.get());
                }
            };
            thread.start();
        }
    }

    // 用于线程共享对象的类
    static class Number{
        private int num ;
        public Number(int num){
            this.num = num;
        }
        public int getNum(){
            return this.num;
        }
        public int incr(){
            return ++this.num;
        }

        @Override
        public String toString() {
            return "Number[num="+num+"]";
        }
    }

}
