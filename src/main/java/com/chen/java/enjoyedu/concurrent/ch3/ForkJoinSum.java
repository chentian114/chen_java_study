package com.chen.java.enjoyedu.concurrent.ch3;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @desc 使用Fork-Join实现数组累加，同步且需获取结果
 * @Author Chentian
 * @date 2021/4/17
 */
public class ForkJoinSum {
    static class MySum extends RecursiveTask<Integer>{

        private Integer[] data;
        private int left;
        private int right;

        public MySum(Integer[] data, int left, int right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        @Override
        protected Integer compute() {
            //拆分子任务结束条件
            if( left == right ){
                System.out.println(Thread.currentThread().getName()+" return left="+left+" right="+right+" result="+data[left]);
                return data[left];
            }

            //使用二分进行拆分子任务
            int mid = left + (right - left)/2;
            MySum leftTask = new MySum(data,left,mid);
            MySum rightTask = new MySum(data,mid+1,right);
            invokeAll(leftTask,rightTask);

            //合并子任务处理结果并返回
            int result = leftTask.join()+rightTask.join();
            System.out.println(Thread.currentThread().getName()+" compute left="+left+" right="+right+" result="+result);
            return result;
        }
    }


    public static void main(String[] args) {

        //生成1,2,...,100的数组
        int num = 100;
        Integer[] data = new Integer[num];
        for (int i = 0 ; i < num ; i++){
            data[i] = i+1;
        }

        //创建线程池运行 fork-join任务
        ForkJoinPool pool = new ForkJoinPool();
        MySum mySum = new MySum(data,0,data.length-1);

        //同步执行
        pool.invoke(mySum);

        //等待计算任务执行完成，获取结果
        int result = mySum.join();
        System.out.println(Thread.currentThread().getName()+" sum[1,2,...100]="+result);

    }

}
