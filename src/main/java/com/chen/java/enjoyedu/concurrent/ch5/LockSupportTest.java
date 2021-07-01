package com.chen.java.enjoyedu.concurrent.ch5;

import java.util.concurrent.locks.LockSupport;

/**
 * @author: Chentian
 * @date: Created in 2021/7/2 6:39
 * @desc
 */
public class LockSupportTest {

    public static void main(String[] args) {

        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName()+" 开始执行。。。");
            LockSupport.park();
            System.out.println(Thread.currentThread().getName()+" 执行结束。");
        };

        Thread thread = new Thread(task);
        thread.start();
        System.out.println(thread.getName()+" 已经启动，但内部进行了 park()");
        LockSupport.unpark(thread);
        System.out.println("使用 LockSupport 进行 unpark()");
    }
}
