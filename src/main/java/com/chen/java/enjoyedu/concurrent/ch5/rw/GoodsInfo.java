package com.chen.java.enjoyedu.concurrent.ch5.rw;

/**
 * @author: Chentian
 * @date: Created in 2021/6/30 7:18
 * @desc
 */
public class GoodsInfo {
    static final int UNITE_PRICE = 25;
    private String name;
    //总销售额
    private double totalMoney;
    //库存数
    private int storeNumber;

    public GoodsInfo(String name, double totalMoney, int storeNumber) {
        this.name = name;
        this.totalMoney = totalMoney;
        this.storeNumber = storeNumber;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public int getStoreNumber() {
        return storeNumber;
    }

    public void changeNumber(int sellNumber){
        this.totalMoney += sellNumber * UNITE_PRICE;
        this.storeNumber -= sellNumber;
    }

    @Override
    public String toString() {
        return "GoodsInfo{" +
                "name='" + name + '\'' +
                ", totalMoney=" + totalMoney +
                ", storeNumber=" + storeNumber +
                '}';
    }
}
