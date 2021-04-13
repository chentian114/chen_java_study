package com.chen.java.enjoyedu.concurrent.ch2;

/**
 * @author: Chentian
 * @date: Created in 2021/4/13 8:21
 * @desc 使用 join()
 */
public class UseJoin {

    static class BeforeRunnable implements Runnable{
        private Thread beforeThread;
        public BeforeRunnable(Thread before){
            this.beforeThread = before;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName()+" is start run, before Thread is:"+ beforeThread.getName());
                beforeThread.join();
                System.out.println(beforeThread.getName()+" is finished! "+Thread.currentThread().getName()+" do something...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName()+" hello join...");
        Thread beforeThread = Thread.currentThread();
        for (int i = 0 ; i < 5 ; i++){
            Thread thread = new Thread(new BeforeRunnable(beforeThread));
            beforeThread = thread;
            thread.start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}
