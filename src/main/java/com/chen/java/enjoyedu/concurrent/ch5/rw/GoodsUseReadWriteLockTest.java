package com.chen.java.enjoyedu.concurrent.ch5.rw;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: Chentian
 * @date: Created in 2021/6/30 7:19
 * @desc 对商品进行读、写业务的应用
 */
public class GoodsUseReadWriteLockTest {
    static final int readWriteRatio = 10;//读写线程的比例
    static final int minThreadCount = 3;//最少线程数

    //读操作任务
    private static class GetTask implements Runnable{
        private GoodsService goodsService;
        public GetTask(GoodsService goodsService){
            this.goodsService = goodsService;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            for (int i = 0 ; i < 100 ; i++){
                goodsService.getNum();
            }
            System.out.println(Thread.currentThread().getName()+"读取商品数据耗时："
                    +(System.currentTimeMillis()-start)+"ms");
        }
    }

    //写操作任务
    private static class WriteThread implements Runnable{
        private GoodsService goodsService;
        public WriteThread(GoodsService goodsService){
            this.goodsService = goodsService;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            Random random = new Random();
            for (int i = 0 ; i < 10 ; i++){
                goodsService.changeNum(random.nextInt());
            }
            System.out.println(Thread.currentThread().getName()
                    +"写商品数据耗时："+(System.currentTimeMillis()-start)+"ms");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        GoodsInfo goodsInfo = new GoodsInfo("apple",100,100000);
        GoodsService goodsService = new UseReadWriteLock(goodsInfo);
        //使用读写锁
        testGetAndChangeGoods(goodsInfo,goodsService);

        TimeUnit.SECONDS.sleep(1);
        System.out.println("==========================");

        goodsInfo = new GoodsInfo("apple",100,100000);
        goodsService = new UseSync(goodsInfo);
        //使用 synchronized
        testGetAndChangeGoods(goodsInfo,goodsService);
    }

    private static void testGetAndChangeGoods(GoodsInfo goodsInfo, GoodsService goodsService) throws InterruptedException {
        for(int i = 0; i< 2; i++){
            Thread setT = new Thread(new WriteThread(goodsService));
            for(int j=0; j< 10;j++) {
                Thread getT = new Thread(new GetTask(goodsService));
                getT.start();
            }
            setT.start();
            TimeUnit.SECONDS.sleep(2);
        }
    }

}
