package com.chen.java.enjoyedu.concurrent.ch4;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @desc
 * @Author Chentian
 * @date 2021/6/28
 */
public class UseAtomicIntegerArray {
    static int[] value = new int[]{1,2};
    static AtomicIntegerArray ai = new AtomicIntegerArray(value);

    public static void main(String[] args) {

        System.out.println(ai.get(0));
        System.out.println(ai.get(1));

        System.out.println(ai.incrementAndGet(0));
        System.out.println(ai.incrementAndGet(1));

        //原数组不会变化
        System.out.println(value[0]);

        ai.getAndSet(0,10);
        System.out.println(ai.get(0));
    }
}
