package com.chen.java.enjoyedu.concurrent.ch4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @desc 使用 AtomicStampedReference 解决ABA问题
 * AtomicStampedReference 参数说明：
 * （1）第一个参数 expectedReference ：表示预期值。
 * （2）第二个参数 newReference ：表示要更新的值。
 * （3）第三个参数 expectedStamp ：表示预期的时间戳。
 * （4）第四个参数 newStamp ：表示要更新的时间戳。
 * @Author Chentian
 * @date 2021/6/28
 */
public class UseAtomicStampedReference {

    static AtomicInteger index = new AtomicInteger(10);
    static AtomicStampedReference<Integer> index2 = new AtomicStampedReference<>(10,1);

    public static void main(String[] args) {

        //产生 ABA 问题
        Runnable abaTask = () -> {
            index.compareAndSet(10,11);
            index.compareAndSet(11,10);
            System.out.println(Thread.currentThread().getName()+" update index: 10 -> 11 -> 10!");

            index2.compareAndSet(10, 11, index2.getStamp(),index2.getStamp()+1);
            index2.compareAndSet(11, 10, index2.getStamp(),index2.getStamp()+1);
            System.out.println(Thread.currentThread().getName()+" update index2: 10 -> 11 -> 10!");
        };
        Thread abaThread = new Thread(abaTask);
        abaThread.start();

        //CAS 原子修改测试
        Runnable updateTask = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                boolean isSuccess = index.compareAndSet(10, 12);
                System.out.println(Thread.currentThread().getName()+": index 预期值是 10 吗: "+isSuccess+
                        "， 设置新值："+index.get());

                isSuccess = index2.compareAndSet(10, 12,index2.getStamp(), index2.getStamp()+1);
                System.out.println(Thread.currentThread().getName()+": index2 修改是否成功: "+isSuccess+
                        "，当前版本是："+index2.getStamp()+"， 当前实际值："+index2.getReference());
            }catch (Exception e){
                e.printStackTrace();
            }
        };
        Thread updateThread = new Thread(updateTask);
        updateThread.start();

    }

}
