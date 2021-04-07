package com.chen.java.enjoyedu.concurrent.ch1;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @author: Chentian
 * @date: Created in 2021/3/31 7:06
 * @desc Java多线程示例
 */
public class OnlyMain {

    public static void main(String[] args) {
        // Java 虚拟机线程系统的管理接口
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        // 获取线程堆栈信息
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        for (ThreadInfo threadInfo : threadInfos){
            System.out.println(threadInfo.getThreadName());
        }
    }
}
