package com.chen.java.enjoyedu.concurrent.ch2;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @desc 使用 ThreadLocal 引发内存泄漏
 * @Author Chentian
 * @date 2021/4/10
 */
public class ThreadLocalOOM {

    private static final int MAX_SIZE = 100;

    //5M大小的数组
    private static class LocalVariable {
        private byte[] value = new byte[1024*1024*5];
    }

    // 创建线程池，固定为5个线程
    private static ThreadPoolExecutor poolExecutor
            = new ThreadPoolExecutor(5,5,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());

    //ThreadLocal共享变量
    private ThreadLocal<LocalVariable> data;

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0 ; i < MAX_SIZE ; i++){
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    //场景1：不执行任何有意义的代码，当所有的任务提交执行完成后，查看内存占用情况，占用16M左右
//                    System.out.println("hello ThreadLocal...");

                    //场景2：创建 数据对象，执行完成后，查看内存占用情况，与场景1相同
//                    new LocalVariable();

                    //场景3：启用 ThreadLocal，执行完成后，查看内存占用情况，占用100M左右
                    ThreadLocalOOM obj = new ThreadLocalOOM();
                    obj.data = new ThreadLocal<>();
                    obj.data.set(new LocalVariable());
                    System.out.println("update ThreadLocal data value..........");

                    //场景4： 加入 remove()，执行完成后，查看内存占用情况，与场景1相同
//                    obj.data.remove();

                    //分析：在场景3中，当启用了ThreadLocal以后确实发生了内存泄漏
                }
            });

            Thread.sleep(100);
        }
        System.out.println("pool execute end!");
    }

}
