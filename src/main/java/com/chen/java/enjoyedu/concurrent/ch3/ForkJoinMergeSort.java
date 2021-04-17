package com.chen.java.enjoyedu.concurrent.ch3;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @desc 使用Fork-Join实现归并排序（倒序），同步且不需要获取结果
 * @Author Chentian
 * @date 2021/4/17
 */
public class ForkJoinMergeSort {

    static class MySort extends RecursiveAction{

        private int[] data;
        private int left;
        private int right;

        public MySort(int[] data, int left, int right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            //拆分子任务结束条件
            if(left == right){
                System.out.println(Thread.currentThread().getName()+" return left="+left+" right="+right+" sort result="+data[left]);
                return;
            }

            //拆分子任务
            int mid = left + (right - left)/2;
            MySort leftTask = new MySort(data,left,mid);
            MySort rightTask = new MySort(data, mid+1, right);
            invokeAll(leftTask,rightTask);

            //等待子任务执行完成
            leftTask.join();
            rightTask.join();

            //合并操作，使用外排
            int[] tmp = new int[right-left+1];
            int leftIndex = left;
            int rightIndex = mid+1;
            for(int i = 0 ; i < tmp.length ; i++){
                if(leftIndex > mid){
                    //左子集已为空
                    tmp[i] = data[rightIndex++];
                }
                else if(rightIndex > right){
                    //右子集已为空
                    tmp[i] = data[leftIndex++];
                }
                else {
                    if(data[leftIndex] > data[rightIndex]){
                        tmp[i] = data[leftIndex++];
                    }
                    else {
                        tmp[i] = data[rightIndex++];
                    }
                }
            }
            //将合并结果拷贝回原数组
            for(int i = 0 ; i < tmp.length; i++){
                data[left+i] = tmp[i];
            }

            StringBuilder sbr = new StringBuilder("[");
            for(int i = left; i <= right; i++){
                sbr.append(data[i]);
                if (i!= right){
                    sbr.append(",");
                }
            }
            sbr.append("]");
            System.out.println(Thread.currentThread().getName()+" compute left="+left+" right="+right+" sort result="+sbr.toString());

        }
    }

    public static void main(String[] args) {
        int[] data ={35,63,48,9,86,24,53,72};

        ForkJoinPool pool = new ForkJoinPool();
        MySort mySort = new MySort(data,0,data.length-1);
        pool.invoke(mySort);

        //等待执行完成
        mySort.join();

        System.out.println(Thread.currentThread().getName()+" sort result:"+ Arrays.toString(data));


    }


}
