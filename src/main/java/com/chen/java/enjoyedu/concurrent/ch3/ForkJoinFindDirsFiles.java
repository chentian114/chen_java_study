package com.chen.java.enjoyedu.concurrent.ch3;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @desc 使用Fork-Join遍历打印文件夹内指定类型文件，异步且不需要获取结果
 * @Author Chentian
 * @date 2021/4/17
 */
public class ForkJoinFindDirsFiles {

    public static String fileType = "java";
    static class MyPrint extends RecursiveAction{

        private String path ;

        public MyPrint(String path) {
            this.path = path;
        }

        @Override
        protected void compute() {

            //拆分子任务终止条件
            File dirFile = new File(path);
            if(!dirFile.isDirectory()){
                if(dirFile.getName().endsWith(fileType)) {
                    System.out.println(Thread.currentThread().getName() + " return fileName="+dirFile.getName());
                }
                return;
            }

            //拆分子任务，如果是文件夹，创建一个子任务
            File[] files = dirFile.listFiles();
            if(files == null){
                return;
            }
            List<MyPrint> myPrintTaskList = new LinkedList<>();
            for(File file: files){
                if(file.isDirectory()){
                    myPrintTaskList.add(new MyPrint(file.getPath()));
                }
                else{
                    if(file.getAbsolutePath().endsWith(fileType)) {
                        System.out.println(Thread.currentThread().getName() + " dir="+dirFile.getName()+" compute fileName="+file.getName());
                    }
                }
            }

            invokeAll(myPrintTaskList);
        }
    }

    public static void main(String[] args) {
        String path ="F:\\chen\\code\\giteeRepo\\chen_java_study\\src\\main\\java\\com\\chen\\java";
        ForkJoinPool pool = new ForkJoinPool();
        MyPrint printTask = new MyPrint(path);
        //异步提交
        pool.execute(printTask);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
