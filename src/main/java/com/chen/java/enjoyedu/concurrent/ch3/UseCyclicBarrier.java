package com.chen.java.enjoyedu.concurrent.ch3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

/**
 * @desc 使用CyclicBarrier
 * @Author Chentian
 * @date 2021/4/17
 * 需求：共4个子线程，他们全部完成工作后，交出自己结果，再被统一释放去做自己的事情，而交出的结果被另外的线程拿来拼接字符串；
 */
public class UseCyclicBarrier {

    public static void main(String[] args) {

        //存放子线程工作结果
        ConcurrentHashMap<String,Long> valMap = new ConcurrentHashMap<>();
        //汇总子线程工作结果任务
        Runnable collectRunnable = new Runnable(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+" collect child thread result start...");
                StringBuilder sbr = new StringBuilder();
                for (String key: valMap.keySet()){
                    sbr.append("[").append(key).append(":").append(valMap.get(key)).append("],");
                }
                String result = sbr.substring(0,sbr.length()-1);
                System.out.println(Thread.currentThread().getName()+" collect child thread result is:"+result);
            }
        };

        //创建栅栏,并指定,子任务执行完成后,额外执行汇总任务
        CyclicBarrier cyclicBarrier = new CyclicBarrier(4, collectRunnable);

        //创建子线程
        for (int i = 0 ; i < 4 ; i++){
            final int index = i;
            Thread thread = new Thread(){
                @Override
                public void run() {

                    try{
                        System.out.println(Thread.currentThread().getName()+" compute val!");
                        Thread.sleep(1000);
                        valMap.put(Thread.currentThread().getName(),Thread.currentThread().getId());
                        cyclicBarrier.await();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    System.out.println(Thread.currentThread().getName()+" do other something...");
                }
            };
            thread.start();
        }

        //等待线程执行完成
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
