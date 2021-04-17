package com.chen.java.enjoyedu.concurrent.ch3;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @desc 使用 FutureTask
 * @Author Chentian
 * @date 2021/4/17
 */
public class UseFutureTask {

    public static void main(String[] args) {

        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Random random = new Random();
                System.out.println(Thread.currentThread().getName()+" run callable....");
                Thread.sleep(1000);
                return random.nextInt(100);
            }
        };

        FutureTask<Integer> futureTask = new FutureTask<>(callable);
        Thread thread = new Thread(futureTask);
        thread.start();

        System.out.println(Thread.currentThread().getName()+" task isDown:"+futureTask.isDone());
        try {
            //获取结果
            System.out.println(Thread.currentThread().getName()+" get result:"+futureTask.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
