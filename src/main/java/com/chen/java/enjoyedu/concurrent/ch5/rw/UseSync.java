package com.chen.java.enjoyedu.concurrent.ch5.rw;

import java.util.concurrent.TimeUnit;

/**
 * @author: Chentian
 * @date: Created in 2021/6/30 7:19
 * @desc 使用 synchronized
 */
public class UseSync implements GoodsService{
    private GoodsInfo goodsInfo;
    public UseSync(GoodsInfo goodsInfo){
        this.goodsInfo = goodsInfo;
    }

    @Override
    public synchronized GoodsInfo getNum() {
        try {
            TimeUnit.MICROSECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return goodsInfo;
    }

    @Override
    public synchronized void changeNum(int number) {
        try {
            TimeUnit.MICROSECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        goodsInfo.changeNumber(number);
    }
}
