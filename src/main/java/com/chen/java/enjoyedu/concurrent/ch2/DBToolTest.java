package com.chen.java.enjoyedu.concurrent.ch2;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Chentian
 * @date: Created in 2021/4/13 0:51
 * @desc 测试数据库连接池，创建50个线程，每个线程执行20次获取数据库连接，统计获取成功、失败连接的次数
 */
public class DBToolTest {

    public static void main(String[] args) {
        //计数器：统计可以拿到连接的线程
        AtomicInteger got = new AtomicInteger();
        //计数器：统计没有拿到连接的线程();
        AtomicInteger notGot = new AtomicInteger();
        //线程执行计数器
        CountDownLatch countDownLatch = new CountDownLatch(50);

        DBPool dbPool = new DBPool();

        for (int i = 0 ; i < 50 ; i++){
            Thread thread = new Thread(){
                @Override
                public void run() {
                    for (int i = 0 ; i < 20 ; i ++) {
                        //获取数据库连接
                        Connection connection = null;
                        try {
                            connection = dbPool.fetchConnection(1000);
                            if (connection != null) {
                                got.incrementAndGet();
                                System.out.println(Thread.currentThread().getName() + " 获取连接成功，执行查询操作...." + connection);
                                //模拟执行
                                connection.createStatement();
                                Thread.sleep(100);
                                connection.commit();
                            } else {
                                notGot.incrementAndGet();
                                System.out.println(Thread.currentThread().getName() + " 获取连接失败！");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //释放连接
                            dbPool.releaseConnection(connection);
                        }
                    }
                    countDownLatch.countDown();
                }
            };
            thread.start();

        }
        //等待所有线程执行完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("总获取连接次数："+(50*20));
        System.out.println("获取成功次数："+got.get());
        System.out.println("获取失败次数："+notGot.get());
    }

}
