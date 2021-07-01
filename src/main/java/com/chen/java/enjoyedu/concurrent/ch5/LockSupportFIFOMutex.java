package com.chen.java.enjoyedu.concurrent.ch5;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * @author: Chentian
 * @date: Created in 2021/7/2 6:23
 * @desc 使用 LockSupport
 * 假设现在需要实现一种FIFO类型的独占锁，可以把这种锁看成是ReentrantLock的公平锁简单版本，且是不可重入的，
 * 就是说当一个线程获得锁后，其它等待线程以FIFO的调度方式等待获取锁。
 *
 */
public class LockSupportFIFOMutex {
    private final boolean LOCKED = true;
    private final boolean UN_LOCKED = false;
    private final AtomicBoolean locked = new AtomicBoolean(UN_LOCKED);
    private final Queue<Thread> waiters = new ConcurrentLinkedQueue<>();

    public void lock(){
        Thread current = Thread.currentThread();
        waiters.add(current);

        // 如果当前线程不在队首，或锁已被占用，则当前线程阻塞(锁必须由队首元素拿到)
        while (waiters.peek() != current || !locked.compareAndSet(UN_LOCKED, LOCKED)){
            LockSupport.park(this);
        }
        waiters.remove();
    }

    public void unlock(){
        locked.set(UN_LOCKED);
        LockSupport.unpark(waiters.peek());
    }

    public static void main(String[] args) {
        LockSupportFIFOMutex mutex = new LockSupportFIFOMutex();

        Thread thread1 = new MyThread("mutex-1",mutex);
        Thread thread2 = new MyThread("mutex-2",mutex);
        Thread thread3 = new MyThread("mutex-3",mutex);

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(MyThread.count);
    }

}

class MyThread extends Thread {
    private String name;
    private LockSupportFIFOMutex mutex;
    public static int count;

    public MyThread(String name, LockSupportFIFOMutex mutex) {
        this.name = name;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            mutex.lock();
            count++;
            System.out.println("name:" + name + "  count:" + count);
            mutex.unlock();
        }
    }
}
