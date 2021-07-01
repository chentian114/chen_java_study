package com.chen.java.enjoyedu.concurrent.ch5.rw;

/**
 * @author: Chentian
 * @date: Created in 2021/6/30 7:18
 * @desc
 */
public interface GoodsService {
    /** 获得商品的信息 */
    GoodsInfo getNum();
    /** 修改商品的数量 */
    void changeNum(int number);

}
