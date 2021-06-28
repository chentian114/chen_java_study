package com.chen.java.enjoyedu.concurrent.ch4;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @desc 类说明：有一个残缺A类实现了线程安全的 get方法和compareAndSet()方法，自行实现它的递增方法
 * @Author Chentian
 * @date 2021/6/28
 */
public class HalfAtomicInt {

    //模拟残缺A类的实例
    private AtomicInteger atomicA = new AtomicInteger(0);

    //模拟残缺A类的 get 方法实现
    public int get() {
        return atomicA.get();
    }

    //模拟残缺A类的 compareAndSet 方法实现
    public boolean compareAndSet(int oldValue,int newValue){
        return atomicA.compareAndSet(oldValue,newValue);
    }

    //实现自增
    public void increament(){
        for (;;){
            int oldVal = get();
            boolean result = compareAndSet(oldVal, ++oldVal);
            if(result){
                break;
            }
        }
    }

    public static void main(String[] args) {
        HalfAtomicInt halfAtomicInt = new HalfAtomicInt();
        Runnable incTask = () -> {
            for (int i = 0 ; i < 10 ; i++){
                halfAtomicInt.increament();
            }
        };

        for (int i = 0 ; i < 10 ; i++){
            new Thread(incTask).start();
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(halfAtomicInt.get());
    }
}
