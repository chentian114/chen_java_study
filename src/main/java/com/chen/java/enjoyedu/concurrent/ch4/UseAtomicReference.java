package com.chen.java.enjoyedu.concurrent.ch4;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @desc
 * @Author Chentian
 * @date 2021/6/28
 */
public class UseAtomicReference {

    static AtomicReference<UserInfo> userAtomicReference;

    public static void main(String[] args) {
        //要修改的实体的实例
        UserInfo tom = new UserInfo("Tom", 18);

        userAtomicReference = new AtomicReference<>(tom);
        System.out.println(userAtomicReference.get());

        UserInfo bill = new UserInfo("Bill", 19);
        userAtomicReference.compareAndSet(tom,bill);
        System.out.println(userAtomicReference.get());

        System.out.println(tom);
    }



    static class UserInfo{
        private volatile String name;
        private int age;

        public UserInfo(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
