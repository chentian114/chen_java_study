package com.chen.java.enjoyedu.concurrent.ch4;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @desc
 * @Author Chentian
 * @date 2021/6/28
 */
public class UseAtomicInt {
    static AtomicInteger num = new  AtomicInteger(0);

    public static void main(String[] args) {

        System.out.println(num.get());
        System.out.println(num.incrementAndGet());
        System.out.println(num.addAndGet(2));
        System.out.println(num.getAndIncrement());
        System.out.println(num.get());

    }
}
