# 并发编程之线程的并发工具类

[toc]

## ForkJoin

**分而治之：**
- ForkJoin 在处理 分而治之的问题 时非常的有用。
- 十大计算机经典算法：快速排序、堆排序、归并排序、二分查找、线性查找、
深度优先、广度优先、Dijkstra、动态规划、朴素贝叶斯分类，属于分而治之的有3个，快速排序、归并排序、二分查找，还有大数据中M/R都是。 
- 分治法的设计思想是：将一个难以直接解决的大问题，分割成一些规模较小的相同问题，以便各个击破，分而治之。
- 分治策略是：对于一个规模为n的问题，若该问题可以容易地解决（比如说规模n较小）则直接解决，否则将其分解为k个规模较小的子问题，这些子问题互相独立且与原问题形式相同(子问题相互之间有联系就会变为动态规划算法)，递归地解这些子问题，然后将各子问题的解合并得到原问题的解。这种算法设计策略叫做分治法。

**归并排序：**
- 归并排序是建立在归并操作上的一种有效的排序算法。该算法是采用分治法的一个非常典型的应用。将已有序的子序列合并，得到完全有序的序列；即先使每个子序列有序，再使子序列段间有序。
- 若将两个有序表合并成一个有序表，称为2-路归并，与之对应的还有多路归并。
- 对于给定的一组数据，利用递归与分治技术将数据序列划分成为越来越小的半子表，在对半子表排序后，再用递归方法将排好序的半子表合并成为越来越大的有序序列。
- 为了提升性能，有时我们在半子表的个数小于某个数（比如15）的情况下，对半子表的排序采用其他排序算法，比如插入排序。

![2.归并排序案例](https://img-blog.csdnimg.cn/20210414075825323.bmp)

**Fork-Join原理:**

![3.Fork-Join原理](https://img-blog.csdnimg.cn/20210414212554151.jpg)

**工作密取：**
- 即当前线程的Task已经全被执行完毕，则自动取到其他线程的Task池中取出Task继续执行。
- ForkJoinPool中维护着多个线程（一般为CPU核数）在不断地执行Task，每个线程除了执行自己职务内的Task之外，还会根据自己工作线程的闲置情况去获取其他繁忙的工作线程的Task，如此一来就能能够减少线程阻塞或是闲置的时间，提高CPU利用率。

**Fork/Join使用的标准范式：**
- 使用ForkJoin框架，必须首先创建一个ForkJoin任务。
- 它提供在任务中执行fork和join的操作机制，通常我们不直接继承ForkjoinTask类，只需要直接继承其子类：
  - RecursiveAction，用于没有返回结果的任务；
  - RecursiveTask，用于有返回值的任务。
- task要通过ForkJoinPool来执行，使用submit 或 invoke 提交，两者的区别是：
  - invoke是同步执行，调用之后需要等待任务完成，才能执行后面的代码；
  - submit是异步执行。
- join()和get()方法当任务完成的时候返回计算结果。

![4.Fork-Join实战](https://img-blog.csdnimg.cn/20210414213251686.jpg)

**Fork-Join使用：**
- 在我们自己实现的compute方法里，首先需要判断任务是否足够小，如果足够小就直接执行任务。 
- 如果不足够小，就必须分割成两个子任务，每个子任务在调用invokeAll方法时，又会进入compute方法，
- 检查当前子任务是否需要继续分割成孙任务，如果不需要继续分割，则执行当前子任务并返回结果。
- 使用join方法会等待子任务执行完并得到其结果。

**代码实践：使用Fork-Join实现数组累加**
- 同步且获取结果
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch3.ForkJoinSum](https://gitee.com/chentian114/chen_java_study)


**代码实践：使用Fork-Join实现归并排序**
- 同步且不需要获取结果
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch3.ForkJoinMergeSort](https://gitee.com/chentian114/chen_java_study)


**代码实践：使用Fork-Join遍历打印文件夹内所有文件**
- 异步不需要获取结果
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch3.ForkJoinFindDirsFiles](https://gitee.com/chentian114/chen_java_study)



## 并发工具类

### CountDownLatch

- CountDownLatch，闭锁；
  - 能够使一个线程等待其他线程完成各自的工作后再执行。
  - 例如，应用程序的主线程希望在负责启动框架服务的线程已经启动所有的框架服务之后再执行。
- 原理：
  - CountDownLatch是通过一个计数器来实现的，计数器的初始值为初始任务的数量。
  - 每当完成了一个任务后，计数器的值就会减1（CountDownLatch.countDown()方法）。
  - 当计数器值到达0时，它表示所有的已经完成了任务，然后在闭锁上等待CountDownLatch.await()方法的线程就可以恢复执行任务。
- 应用场景：
  - 实现最大的并行性：有时我们想同时启动多个线程，实现最大程度的并行性；例如创建一个初始计数为1的CountDownLatch，并让所有线程都在这个锁上等待，只需调用 一次countDown()方法就可以让所有的等待线程同时恢复执行。
  - 开始执行前等待n个线程完成各自任务；例如应用程序启动类要确保在处理用户请求前，所有N个外部系统已经启动和运行了。

**CountDownLatch使用示例：**

![5.UseCountDownLatch](https://img-blog.csdnimg.cn/20210414220051881.jpg)

**代码实践：使用CountDownLatch**
- 需求：共5个初始化子线程，6个闭锁扣除点，扣除完毕后，主线程和业务线程才能继续执行；
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch3.UseCountDownLatch](https://gitee.com/chentian114/chen_java_study)

### CyclicBarrier

- CyclicBarrier
  - 字面意思是可循环使用（Cyclic）的屏障（Barrier）。
  - 它要做的事情是，让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续运行。
- 使用：
  - CyclicBarrier默认的构造方法是CyclicBarrier（int parties），其参数表示屏障拦截的线程数量，
  - 每个线程调用await方法告诉CyclicBarrier我已经到达了屏障，然后当前线程被阻塞。
  - CyclicBarrier还提供一个更高级的构造函数CyclicBarrier（int parties，Runnable barrierAction），用于在线程到达屏障时，优先执行barrierAction，方便处理更复杂的业务场景。
- 应用场景：
  - CyclicBarrier可以用于多线程计算数据，最后合并计算结果的场景。 

**CyclicBarrier使用示例：**

![6.UseCyclicBarrier](https://img-blog.csdnimg.cn/20210414220207187.jpg)

**代码实践：使用CyclicBarrier**
- 需求：共4个子线程，他们全部完成工作后，交出自己结果，再被统一释放去做自己的事情，而交出的结果被另外的线程拿来拼接字符串；
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch3.UseCyclicBarrier](https://gitee.com/chentian114/chen_java_study)

**CountDownLatch和CyclicBarrier辨析：**
- CountDownLatch的计数器只能使用一次，而CyclicBarrier的计数器可以反复使用。
- CountDownLatch.await()一般阻塞工作线程，所有的进行预备工作的线程执行countDown()，而CyclicBarrier通过工作线程调用await()从而自行阻塞，直到所有工作线程达到指定屏障，再大家一起往下走。
- 在控制多个线程同时运行上，CountDownLatch可以不限线程数量，而CyclicBarrier是固定线程数。
- 同时，CyclicBarrier还可以提供一个barrierAction，合并多线程计算结果。

### Semaphore

- Semaphore（信号量）
  - 用来控制同时访问特定资源的线程数量，
  - 它通过协调各个线程，以保证合理的使用公共资源。
- 应用场景：
  - 可以用于做流量控制，特别是公用资源有限的应用场景，比如数据库连接。 
  - 例：有一个需求，要读取几万个文件的数据，因为都是IO密集型任务，我们可以启动几十个线程并发地读取，但是如果读到内存后，还需要存储到数据库中，而数据库的连接数只有10个，这时我们必须控制只有10个线程同时获取数据库连接保存数据，否则会报错无法获取数据库连接。可以使用Semaphore来做流量控制。
- 使用：
  - 构造方法Semaphore（int permits）接受一个整型的数字，表示可用的许可证数量。 
  - 用法：首先线程使用Semaphore的acquire()方法获取一个许可证，使用完之后调用release()方法归还许可证。
  - 还可以用tryAcquire()方法尝试获取许可证。
- Semaphore还提供一些其他方法，具体如下：
  - int availablePermits()：返回此信号量中当前可用的许可证数。
  - int getQueueLength()：返回正在等待获取许可证的线程数。
  - boolean hasQueuedThreads()：是否有线程正在等待获取许可证。
  - void reducePermits（int reduction）：减少reduction个许可证，是个protected方法。
  - Collection getQueuedThreads()：返回所有等待获取许可证的线程集合，是个protected方法。

**FairSync(公平锁)和NonFairSync(非公平锁)：**
- 如果一个线程组里，能保证每个线程都能拿到锁，那么这个锁就是公平锁。相反，如果保证不了每个线程都能拿到锁，也就是存在有线程饿死，那么这个锁就是非公平锁。
- 公平锁的实现机理在于每次有线程来抢占锁的时候，都会检查一遍有没有等待队列；
- 非公平锁：在等待锁的过程中， 如果有任意新的线程妄图获取锁，都是有很大的几率直接获取到锁的。
- 非公平锁与公平锁的区别在于新晋获取锁的进程会有多次机会去抢占锁。如果被加入了等待队列后则跟公平锁没有区别。
- 公平和非公平锁的队列都基于锁内部维护的一个双向链表，表结点Node的值就是每一个请求当前锁的线程。公平锁则在于每次都是依次从队首取值。
- 公平锁和非公平锁在锁的获取上都使用到了 volatile 关键字修饰的state字段， 这是保证多线程环境下锁的获取与否的核心。
- ReentrantLock 通过 volatile 和 CAS 的搭配实现锁的功能。
- ReenTrantLock实现了公平锁和非公平锁。
- ReentrantLock的实现是基于其内部类FairSync(公平锁)和NonFairSync(非公平锁)实现的。 其可重入性是基于Thread.currentThread()实现的: 如果当前线程已经获得了执行序列中的锁， 那执行序列之后的所有方法都可以获得这个锁。

**Semaphore源码解读：Semaphore初始化**
- 当调用new Semaphore(2) 方法时，默认会创建一个非公平的锁的同步阻塞队列。
- 把初始令牌数量赋值给同步队列的state状态，state的值就代表当前所剩余的令牌数量。

**Semaphore源码解读：semaphore.acquire();**
- 获取一个令牌，获取令牌的过程也就是使用原子的操作去修改同步队列的state ,获取一个令牌则修改为state=state-1。
- 当计算出来的state<0，则代表令牌数量不足，此时会创建一个Node节点加入阻塞队列，挂起当前线程。
- 当计算出来的state>=0，则代表获取令牌成功。
```
// 获取1个令牌
public void acquire() throws InterruptedException {
    sync.acquireSharedInterruptibly(1);
}

//共享模式下获取令牌，获取成功则返回，失败则加入阻塞队列，挂起线程
public final void acquireSharedInterruptibly(int arg)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    //尝试获取令牌，arg为获取令牌个数，当可用令牌数减当前令牌数结果小于0,
    //则创建一个节点加入阻塞队列，挂起当前线程。
    if (tryAcquireShared(arg) < 0)
        doAcquireSharedInterruptibly(arg);
}
```

**Semaphore源码解读：semaphore.release();**
- 线程会尝试释放一个令牌，释放令牌的过程也就是把同步队列的state修改为state=state+1的过程；
- 释放令牌成功之后，同时会唤醒同步队列的所有阻塞节共享节点线程；
- 被唤醒的节点会重新尝试去修改state=state-1 的操作，如果state>=0则获取令牌成功，否则重新进入阻塞队列，挂起线程。
```
// 释放令牌
public void release() {
    sync.releaseShared(1);
}

// 释放共享锁，同时唤醒所有阻塞队列共享节点线程
public final boolean releaseShared(int arg) {
    //释放共享锁
    if (tryReleaseShared(arg)) {
        //唤醒所有共享节点线程
        doReleaseShared();
        return true;
    }
    return false;
}
```

**Semaphore 使用示例：**

![7.UseSemaphore](https://img-blog.csdnimg.cn/20210414221127838.jpg)

代码实践：使用Semaphore实现数据库连接池
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch3.DBPoolSemaphore](https://gitee.com/chentian114/chen_java_study)
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch3.DBPoolNoUseless](https://gitee.com/chentian114/chen_java_study)

### Exchange

- Exchanger（交换者）
  - 是一个用于线程间协作的工具类。
  - 用于进行线程间的数据交换。
  - 它提供一个同步点，在这个同步点，两个线程可以交换彼此的数据。
- 使用：
  - 两个线程通过 exchange() 方法交换数据，
  - 如果第一个线程先执行exchange()方法，它会一直等待第二个线程也执行exchange()方法，
  - 当两个线程都到达同步点时，这两个线程就可以交换数据，将本线程生产出来的数据传递给对方。 

**Exchange 使用示例：**

![8.UseExchange](https://img-blog.csdnimg.cn/20210414224409433.jpg)

**代码实践：使用Exchange**
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch3.UseExchange](https://gitee.com/chentian114/chen_java_study)

### Callable、Future和FutureTask

Runnable是一个接口，在它里面只声明了一个run()方法，由于run()方法返回值为void类型，所以在执行完任务之后无法返回任何结果。

Callable位于java.util.concurrent包下，它也是一个接口，在它里面也只声明了一个方法 call()，这是一个泛型接口，call()函数返回的类型就是传递进来的V类型。

Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果。必要时可以通过get方法获取执行结果，该方法会阻塞直到任务返回结果。

因为Future只是一个接口，所以是无法直接用来创建对象使用的，因此需要使用 FutureTask。

FutureTask类实现了RunnableFuture接口，RunnableFuture继承了Runnable接口和Future接口，而FutureTask实现了RunnableFuture接口。所以它既可以作为Runnable被线程执行，又可以作为Future得到Callable的返回值。

通过一个线程运行Callable，但是Thread不支持构造方法中传递Callable的实例，所以我们需要通过FutureTask把一个Callable包装成Runnable，然后再通过这个FutureTask拿到Callable运行后的返回值。

**代码实践：使用 FutureTask**
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch3.UseFutureTask](https://gitee.com/chentian114/chen_java_study)

## 相关链接

gitee地址：[https://gitee.com/chentian114/chen_java_study](https://gitee.com/chentian114/chen_java_study)

github地址：[https://github.com/chentian114/chen_java_study](https://github.com/chentian114/chen_java_study)

CSDN地址：[https://blog.csdn.net/chentian114/category_10956933.html](https://blog.csdn.net/chentian114/category_10956933.html)

## 公众号

![知行chen](https://img-blog.csdnimg.cn/20201019220227866.jpg)

## 参考

享学 Java课程 Mark

Semaphore源码解读: https://zhuanlan.zhihu.com/p/98593407