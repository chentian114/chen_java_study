package com.chen.java.enjoyedu.concurrent.ch5;

import java.util.concurrent.TimeUnit;

/**
 * @author: Chentian
 * @date: Created in 2021/6/30 6:27
 * @desc 测试Lock和Condition实现等待通知
 */
public class ExpressCondTest {

    //创建初始快递
    private static ExpressCondOneLock express = new ExpressCondOneLock(0,"GuangZhou");

    //业务线程-检查里程数变化，是否到达指定阈值
    private static class CheckKm extends Thread{
        @Override
        public void run() {
            express.waitKm();
        }
    }

    //业务线程-检查位置变化，是否到达目的地
    private static class CheckSite extends Thread{
        @Override
        public void run() {
            express.waitSite();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //启动多个业务线程
        for (int i = 0 ; i < 3 ; i++){
            CheckKm thread = new CheckKm();
            thread.setName("checkKm-"+i);
            thread.start();
        }
        for (int i = 0 ; i < 3 ; i++){
            CheckSite thread = new CheckSite();
            thread.setName("checkSite-"+i);
            thread.start();
        }

      //变化里程数
        TimeUnit.SECONDS.sleep(1);
        express.changeKm(89);
        TimeUnit.SECONDS.sleep(1);
        express.changeKm(ExpressCondOneLock.KM_NUM);

        //变化位置
        TimeUnit.SECONDS.sleep(1);
        express.changeSite(ExpressCondOneLock.CITY);
    }

}
