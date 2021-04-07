package com.chen.java.enjoyedu.concurrent.ch1;

import java.util.concurrent.*;

/**
 * @author: Chentian
 * @date: Created in 2021/3/31 7:11
 * @desc 使用三种方式创建线程
 */
public class NewThread {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //方式一：继承 Thread
        Thread thread1 = new MyThread();
        thread1.start();
        System.out.println("--------------------");

        //方式二：实现 Runnable
        Thread thread2 = new Thread(new MyRunnable());
        thread2.start();
        System.out.println("--------------------");

        //方式三：实现 Callable
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
        Thread thread3 = new Thread(futureTask);
        thread3.start();
        System.out.println("is Down:"+futureTask.isDone());
        Thread.sleep(800);
        System.out.println("is Down:"+futureTask.isDone());
        if(futureTask.isDone()){
            System.out.println("future get:"+futureTask.get());
        }
    }

}

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Hello World! MyThread!");
    }
}

class MyRunnable implements Runnable{

    @Override
    public void run() {
        System.out.println("Hello World! MyRunnable!");
    }
}

class MyCallable implements Callable<Integer>{

    @Override
    public Integer call() throws Exception {
        System.out.println("Hello World! MyCallable!");
        Thread.sleep(500);
        return 1;
    }
}