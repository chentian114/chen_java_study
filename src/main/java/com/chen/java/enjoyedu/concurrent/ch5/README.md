# 并发编程之显示锁和LockSupport

[toc]


## Lock

**有了synchronized为什么还要Lock？**
- Java程序是靠synchronized关键字实现锁功能的，
- 使用synchronized关键字将会隐式地获取锁，
- 但是它将锁的获取和释放固化了，也就是先获取再释放。

**显示锁常见的特性：**
- 尝试非阻塞地获取锁
  - 当前线程尝试获取锁，如果这一时刻锁没有被其它线程获取到，则成功获取并持有锁； 
- 能被中断地获取锁
  - 与 synchronized 不同，获取到锁的线程能够响应中断，当获取到锁的线程被中断时，中断异常将会被抛出，同时锁会被释放； 
- 超时获取锁
  - 在指定的截止时间之前获取锁，如果截止时间到了仍旧无法获取锁，则返回。 

**Lock 的标准用法：**
```
  lock.lock();
  try{
      //...
  }finally{
      lock.unlock();
  }
```
- 在 finally 块中释放锁，目的是保证在获取到锁之后，最终能够被释放。
- 不要将获取锁的过程写在 try 块中，因为如果在获取锁（自定义锁的实现）时发生了异常，异常抛出的同时，也会导致锁无故释放。


**Lock 的常用 API ：**
- void lock()
  - 获取锁，调用该方法当前线程将会获取锁，当锁获得后，从该方法返回；
- void lockInterruptibly() throws InterruptedException
  - 可中断地获取锁，和 lock() 方法的不同之处在于该方法会响应中断，即在锁的获取中可以中断当前线程； 
- boolean tryLock()
  - 尝试非阻塞的获取锁，调用该方法后立刻返回，如果能够获取则返回 true ，否则返回 false ； 
- boolean tryLock(long time, TimeUnit unit) throws InterruptedException
  - 超时的获取锁，当前线程在以下3种情况下返回：
    - 1.当前线程在超时时间内获得了锁；
    - 2.当前线程在超时时间内被中断；
    - 3.超时时间结束，返回 false 。
- void unlock()
  - 释放锁。

## ReentrantLock

**锁的可重入：**
- 简单地讲就是：“同一个线程对于已经获得到的锁，可以多次继续申请到该锁的使用权”。
- synchronized 关键字隐式的支持重进入，比如一个 synchronized 修饰的递归方法，在方法执行时，执行线程在获取了锁之后仍能连续多次地获得该锁。
- ReentrantLock 在调用lock()方法时，已经获取到锁的线程，能够再次调用 lock() 方法获取锁而不被阻塞。


**公平锁和非公平锁：**
- 如果在时间上，先对锁进行获取的请求一定先被满足，那么这个锁是公平的，反之，是不公平的。
- 公平的获取锁，也就是等待时间最长的线程最优先获取锁，也可以说锁获取是顺序的。
- ReentrantLock提供了一个构造函数，能够控制锁是否是公平的。
- 事实上，公平的锁机制往往没有非公平的效率高。
- 在激烈竞争的情况下,非公平锁的性能高于公平锁的性能的一个原因是:
  - 在恢复一个被挂起的线程与该线程真正开始运行之间存在着严重的延迟。
- 例：假设线程A持有一个锁,并且线程B请求这个锁。
  - 由于这个锁已被线程A持有,因此B将被挂起。
  - 当A释放锁时,B将被唤醒,因此会再次尝试获取锁。
  - 与此同时,如果C也请求这个锁,那么C很可能会在B被完全唤醒之前获得、使用以及释放这个锁。
  - 这样的情况是一种“双赢”的局面:B获得锁的时刻并没有推迟,C更早地获得了锁,并且吞吐量也获得了提高。

**FairSync(公平锁)和NonFairSync(非公平锁)：**
- 如果一个线程组里，能保证每个线程都能拿到锁，那么这个锁就是公平锁。相反，如果保证不了每个线程都能拿到锁，也就是存在有线程饿死，那么这个锁就是非公平锁。
- 公平锁的实现机理在于每次有线程来抢占锁的时候，都会检查一遍有没有等待队列；
- 非公平锁：在等待锁的过程中， 如果有任意新的线程妄图获取锁，都是有很大的几率直接获取到锁的。
- 非公平锁与公平锁的区别在于新晋获取锁的线程会有多次机会去抢占锁。如果被加入了等待队列后则跟公平锁没有区别。
- 公平和非公平锁的队列都基于锁内部维护的一个双向链表，表结点Node的值就是每一个请求当前锁的线程。公平锁则在于每次都是依次从队首取值。
- 公平锁和非公平锁在锁的获取上都使用到了 volatile 关键字修饰的 state 字段， 这是保证多线程环境下锁的获取与否的核心。
- ReentrantLock 通过 volatile 和 CAS 的搭配实现锁的功能。
- ReenTrantLock 实现了公平锁和非公平锁。
- ReentrantLock 的实现是基于其内部类 FairSync (公平锁)和 NonFairSync (非公平锁)实现的。 其可重入性是基于 Thread.currentThread() 实现的: 如果当前线程已经获得了执行序列中的锁， 那执行序列之后的所有方法都可以获得这个锁。

**读写锁 ReentrantReadWriteLock ：**
- 之前提到锁（如 Mutex 和 ReentrantLock ）基本都是排他锁，这些锁在同一时刻只允许一个线程进行访问；
- 而读写锁在同一时刻可以允许多个读线程访问，但是在写线程访问时，所有的读线程和其他写线程均被阻塞。
- 读写锁维护了一对锁，一个读锁和一个写锁，通过分离读锁和写锁，使得并发性相比一般的排他锁有了很大提升。
- 除了保证写操作对读操作的可见性以及并发性的提升之外，读写锁能够简化读写交互场景的编程方式。
  - 假设在程序中定义一个共享的用作缓存数据结构，它大部分时间提供读服务（例如查询和搜索），而写操作占有的时间很少，但是写操作完成之后的更新需要对后续的读服务可见。
  - 在没有读写锁支持的（Java 5之前）时候，如果需要完成上述工作就要使用Java的等待通知机制，就是当写操作开始时，所有晚于写操作的读操作均会进入等待状态，只有写操作完成并进行通知之后，所有等待的读操作才能继续执行（写操作之间依靠synchronized关键进行同步），这样做的目的是使读操作能读取到正确的数据，不会出现脏读。
  - 改用读写锁实现上述功能，只需要在读操作时获取读锁，写操作时获取写锁即可。当写锁被获取到时，后续（非当前写操作线程）的读写操作都会被阻塞，写锁释放之后，所有操作继续执行，编程方式相对于使用等待通知机制的实现方式而言，变得简单明了。
- 一般情况下，读写锁的性能都会比排它锁好，因为大多数场景读是多于写的。在读多于写的情况下，读写锁能够提供比排它锁更好的并发性和吞吐量。
- ReentrantReadWriteLock 其实实现的是 ReadWriteLock 接口。

**代码实践：使用读写锁**
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch5.rw.GoodsUseReadWriteLockTest](https://gitee.com/chentian114/chen_java_study)



## Condition 接口

**Condition 常用方法：**
- void await() throws InterruptedException
  - 当线程进入等待状态直到被通知(signal) 或中断，当前线程进入运行状态且从 await() 方法返回的情况，包括：
    - 其它线程调用该 Condition 的 signal() 或 signalAll() 方法，而当前线程被选中唤醒；
    - 其它线程（调用 interrupt() 方法）中断当前线程；
    - 如果当前等待线程从 await() 方法返回，那么表明该线程已经获取了 Condition 对象反对应的锁。
- void awaitUninterruptibly()
  - 当线程进入等待状态直到被通知，从方法名称上可以看出该方法中断不敏感； 
- long awaitNanos(long nanosTimeout) throws InterruptedException
  - 当前线程进入等待状态直到被通知、中断或超时。返回值表示剩余的时间，如果在 nanosTimeout 纳秒之前被唤醒，那么返回值就是(nanosTimeout-实际耗时)。如果返回值是 0 或者负数，那么可以认定已经超时了； 
- boolean awaitUntil(Date deadline) throws InterruptedException
  - 当前线程进入等待状态直到被通知、中断或者到某个时间。如果没有到指定时间就被通知，方法返回 true ，否则，表示到了指定时间，方法返回 false ； 
- void signal()
  - 唤醒一个等待在 Condition 上的线程，该线程从等待方法返回前必须获得与 Condition 相关联的锁； 
- void signalAll()
  - 唤醒所有等待在 Condition 上的线程，能够从等待方法返回的线程必须获得与 Condition 相关联的锁。 


**Condition 使用范式：**

```
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();

public void conditionWait() throws InterruptedException{
    lock.lock();
    try{
        condition.await();
    }finally{
        lock.unlock();
    }
}

public void conditionSignal() throws InterruptedException{
    lock.lock();
    try{
        condition.signal();
    }finally{
        lock.unlock();
    }
}
```

**代码实践：使用ReentrantLock + Condition 实现等待通知：**
- 需求说明：
  - 快递运输的两个参数：快递运输里程数、快递到达地点；
  - 变化运输里程数，然后通知处于wait状态并需要处理运输里程数变化的线程进行业务处理；
  - 业务处理线程-当快递的里程数变化到一个指这阈值（大于100）时进行业务操作（更新数据库）；
  - 变化地点，然后通知处于wait状态并需要处理地点变化的线程进行业务处理；
  - 业务处理线程-当快递变化地点到达目的地时通知用户；
- [com.chen.java.enjoyedu.concurrent.ch5.ExpressCondOneLock](https://gitee.com/chentian114/chen_java_study)
- [com.chen.java.enjoyedu.concurrent.ch5.ExpressCondTest](https://gitee.com/chentian114/chen_java_study)


## LockSupport

LockSupport类，是JUC包中的一个工具类，是用来创建锁和其他同步类的基本线程阻塞原语。

LockSupport 定义了一组的公共静态方法，这些方法提供了最基本的线程阻塞和唤醒功能，而 LockSupport 也成为构建同步组件的基础工具。

LockSupport 定义了一组以 park 开头的方法用来阻塞当前线程，
  - park()
  - park(Object blocker)、
  - parkNanos(Object blocker,long nanos)
  - parkUntil(Object blocker,long deadline)
  - 其中参数 blocker 是用来标识当前线程在等待的对象（记录线程被阻塞时被谁阻塞的）其实就是当前线程调用时所在调用对象，该对象主要用于问题排查和系统监控。

使用
- unpark(Thread thread) 方法来唤醒一个被阻塞的指定线程。

park方法是会响应中断的，但是不会抛出异常。(也就是说如果当前调用线程被中断，则会立即返回但不会抛出中断异常)

LockSupport类使用了一种名为Permit（许可）的概念来做到阻塞和唤醒线程的功能，初始时，permit为0，当调用unpark()方法时，线程的permit加1，当调用park()方法时，如果permit为0，则调用线程进入阻塞状态。


**代码实践：使用 LockSupport**
- 参考代码： [com.chen.java.enjoyedu.concurrent.ch5.LockSupportTest](https://gitee.com/chentian114/chen_java_study)


**代码实践： LockSupport 响应 interrupt**
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch5.LockSupportInterrupt](https://gitee.com/chentian114/chen_java_study)

**代码实践：**
- 需求：假设现在需要实现一种FIFO类型的独占锁，可以把这种锁看成是ReentrantLock的公平锁简单版本，且是不可重入的，就是说当一个线程获得锁后，其它等待线程以FIFO的调度方式等待获取锁。
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch5.LockSupportFIFOMutex](https://gitee.com/chentian114/chen_java_study)


**与wait/notify对比：**
- wait() 和 notify() 都是Object中的方法,在调用这两个方法前必须先获得锁对象，但是 park() 不需要获取某个对象的锁就可以锁住线程。
- notify() 只能随机选择一个线程唤醒，无法唤醒指定的线程， unpark() 却可以唤醒一个指定的线程。

**源码分析：**

```
public static void park() {
    UNSAFE.park(false, 0L);
}

public static void unpark(Thread thread) {
    if (thread != null)
        UNSAFE.unpark(thread);
}
```

## 自旋锁

**概述：**
- 自旋锁它是为实现保护共享资源而提出的一种锁机制。
- 其实，自旋锁与互斥锁比较类似，它们都是为了解决对某项资源的互斥使用。
- 无论是互斥锁，还是自旋锁，在任何时刻，最多只能有一个保持者，也就说，在任何时刻最多只能有一个执行单元获得锁。
- 但是两者在调度机制上略有不同。
  - 对于互斥锁，如果资源已经被占用，资源申请者只能进入睡眠状态。
  - 但是自旋锁不会引起调用者睡眠，如果自旋锁已经被别的执行单元保持，调用者就一直循环在那里看是否该自旋锁的保持者已经释放了锁。


**原理：**
- 一个执行单元要想访问被自旋锁保护的共享资源，必须先得到锁，在访问完共享资源后，必须释放锁。
- 如果在获取自旋锁时，没有任何执行单元保持该锁，那么将立即得到锁；
- 如果在获取自旋锁时锁已经有保持者，那么获取锁操作将自旋在那里，直到该自旋锁的保持者释放了锁。
- 这种锁可能存在两个问题：
  -  死锁。试图递归地获得自旋锁必然会引起死锁：递归程序的持有实例在第二个实例循环，以试图获得相同自旋锁时，不会释放此自旋锁。此外如果一个进程已经将资源锁定，那么，即使其它申请这个资源的进程不停地疯狂“自旋”,也无法获得资源，从而进入死循环。
  - 过多占用cpu资源。如果不加限制，由于申请者一直在循环等待，因此自旋锁在锁定的时候,如果不成功,不会睡眠,会持续的尝试,单cpu的时候自旋锁会让其它process动不了. 因此，一般自旋锁实现会有一个参数限定最多持续尝试次数. 超出后, 自旋锁放弃当前time slice. 等下一次机会。

**适用场景：**
- 自旋锁比较适用于锁使用者保持锁时间比较短的情况。
- 正是由于自旋锁使用者一般保持锁时间非常短，因此选择自旋而不是睡眠是非常必要的，自旋锁的效率远高于互斥锁。



## CLH队列锁 和 MCSLock

自旋锁：线程获取锁的时候，如果锁被其他线程持有，则当前线程将循环等待，直到获取到锁。

自旋锁等待期间，线程的状态不会改变，线程一直是用户态并且是活动的(active)。

自旋锁如果持有锁的时间太长，则会导致其它等待获取锁的线程耗尽CPU。

自旋锁本身无法保证公平性，同时也无法保证可重入性。

基于自旋锁，可以实现具备公平性和可重入性质的锁。

TicketLock:采用类似银行排号叫好的方式实现自旋锁的公平性，但是由于不停的读取serviceNum，每次读写操作都必须在多个处理器缓存之间进行缓存同步，这会导致繁重的系统总线和内存的流量，大大降低系统整体的性能。

CLH队列锁即 Craig, Landin, and Hagersten (CLH) locks。

CLH队列锁也是一种基于链表的可扩展、高性能、公平的自旋锁，申请线程仅仅在本地变量上自旋，它不断轮询前驱的状态，假设发现前驱释放了锁就结束自旋，获得锁。

CLHLock和 MCSLock通过链表的方式避免了减少了处理器缓存同步，极大的提高了性能，区别在于CLHLock是通过轮询其前驱节点的状态，而MCS则是查看当前节点的锁状态。

CLHLock在NUMA架构下使用会存在问题。在没有cache的NUMA系统架构中，由于CLHLock是在当前节点的前一个节点上自旋,NUMA架构中处理器访问本地内存的速度高于通过网络访问其他节点的内存，所以CLHLock在NUMA架构上不是最优的自旋锁。



## 相关链接

gitee地址：[https://gitee.com/chentian114/chen_java_study](https://gitee.com/chentian114/chen_java_study)

github地址：[https://github.com/chentian114/chen_java_study](https://github.com/chentian114/chen_java_study)

CSDN地址：[https://blog.csdn.net/chentian114/category_10956933.html](https://blog.csdn.net/chentian114/category_10956933.html)

## 公众号

![知行chen](https://img-blog.csdnimg.cn/20201019220227866.jpg)

## 参考

享学 Java课程 Mark

https://baike.baidu.com/item/%E8%87%AA%E6%97%8B%E9%94%81

https://www.jianshu.com/p/9d3660ad4358