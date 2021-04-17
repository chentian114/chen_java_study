package com.chen.java.enjoyedu.concurrent.ch3;

import com.chen.java.enjoyedu.concurrent.ch2.DBConnectionImpl;
import com.chen.java.enjoyedu.concurrent.ch2.DBPool;

import java.sql.Connection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @desc 使用Semaphore实现数据库连接池
 * @Author Chentian
 * @date 2021/4/17
 */
public class DBPoolSemaphore {

    //默认连接数量
    private static int DEFAULT_SIZE = 10;
    //存放数据库连接链表
    private ConcurrentLinkedQueue<Connection> connectionList = new ConcurrentLinkedQueue<>();
    //连接池已有连接资源信号量
    private Semaphore useful;
    //连接池可释放连接资源信号量
    private Semaphore useless;

    public DBPoolSemaphore(int size){
        if(size <= 0){
            throw new IllegalArgumentException("size is error!");
        }
        for (int i = 0 ; i < size ; i++){
            connectionList.add(DBConnectionImpl.fetchConnection());
        }
        //设置可取连接数
        useful = new Semaphore(size);
        //设置可释放连接数
        useless = new Semaphore(0);
    }

    public DBPoolSemaphore(){
        this(DEFAULT_SIZE);
    }

    //获取连接
    public Connection fetchConnection(long mills) throws InterruptedException {
        try{
            //申请资源
            boolean result = false;
            //一直等待
            if(mills <= 0 ){
                while (!result){
                    result = useful.tryAcquire(mills, TimeUnit.MILLISECONDS);
                }
            }else{
                result = useful.tryAcquire(mills, TimeUnit.MILLISECONDS);
                if(!result){
                    return null;
                }
            }

            //可释放连接增加
            useless.release();
            //返回连接
            return  connectionList.remove();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //释放连接
    public void releaseConnection(Connection connection){
        try{
            if(connection == null){
                return;
            }

            //申请可释放连接资源
            useless.acquire();

            connectionList.offer(connection);
            //可获取连接资源增加
            useful.release();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}