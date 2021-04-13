package com.chen.java.enjoyedu.concurrent.ch2;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * @author: Chentian
 * @date: Created in 2021/4/13 0:27
 * @desc 模拟实现 数据库连接池
 */
public class DBPool {

    //数据库连接最大容量
    private static final int MAX_SIZE = 10;
    //数据库连接池
    private final static LinkedList<Connection> connectionPool= new LinkedList<>();

    //初始化连接池
    public DBPool(){
        for (int i = 0 ; i < MAX_SIZE; i++){
            connectionPool.add(DBConnectionImpl.fetchConnection());
        }
    }

    //获取连接
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (connectionPool){
            if(mills <= 0) {
                while (connectionPool.isEmpty()) {
                    connectionPool.wait();
                }
                return connectionPool.removeFirst();
            }
            else {
                long expireTime = mills + System.currentTimeMillis();
                long remaining = mills;
                while (connectionPool.isEmpty() && remaining >0){
                    connectionPool.wait(remaining);
                    remaining = expireTime - System.currentTimeMillis();
                }
                if(!connectionPool.isEmpty()){
                    return connectionPool.removeFirst();
                }
                return null;
            }
        }
    }

    //释放连接
    public void releaseConnection(Connection connection){
        if(connection != null){
            synchronized (connectionPool) {
                connectionPool.addLast(connection);
                connectionPool.notifyAll();
            }
        }
    }

}
