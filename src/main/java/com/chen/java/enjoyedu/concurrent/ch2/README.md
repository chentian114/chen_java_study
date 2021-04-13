# 并发编程之线程之间的共享和协作

[toc]

## 线程间的共享

**synchronized内置锁：**
- 用处
  - Java支持多个线程同时访问一个对象或者对象的成员变量，
  - 关键字synchronized可以修饰方法或者以同步块的形式来进行使用，
  - 它主要确保多个线程在同一个时刻，只能有一个线程处于方法或者同步块中，
  - 它保证了线程对变量访问的可见性和排他性（原子性、可见性、有序性），又称为内置锁机制。 
- 对象锁和类锁
  - 对象锁是用于对象实例方法，或者一个对象实例上的，
  - 类锁是用于类的静态方法或者一个类的class对象上的。 
  - 类的对象实例可以有很多个，但是每个类只有一个class对象，所以不同对象实例的对象锁是互不干扰的，但是每个类只有一个类锁。
  - 注意的是，其实类锁只是一个概念上的东西，并不是真实存在的，类锁其实锁的是每个类的对应的class对象。
  - 类锁和对象锁之间也是互不干扰的。

**代码实践：对象锁和类锁实践**
- [com.chen.java.enjoyedu.concurrent.ch2.SyncClzAndInst](https://gitee.com/chentian114/chen_java_study)

**代码实践：错误的加锁和原因分析**

**volatile关键字：**
- 最轻量的同步机制，保证可见性，不保证原子性
- volatile保证了不同线程对这个变量进行操作时的可见性，即一个线程修改了某个变量的值，这新值对其他线程来说是立即可见的。 
- volatile最适用的场景：只有一线程写，多个线程读的场景

**代码实践：volatile 使用**
- [com.chen.java.enjoyedu.concurrent.ch2.VolatileCase](https://gitee.com/chentian114/chen_java_study)

**代码实践：volatile 无法保证原子性**
- [com.chen.java.enjoyedu.concurrent.ch2.VolatileUnsafe](https://gitee.com/chentian114/chen_java_study)

**ThreadLocal：**
- ThreadLocal 和 Synchonized 都用于解决多线程并发访问。
- 可是ThreadLocal与synchronized有本质的差别：
  - synchronized是利用锁的机制，使变量或代码块在某一时该仅仅能被一个线程访问。
  - 而ThreadLocal为每个线程都提供了变量的副本，使得每个线程在某一时间访问到的并非同一个对象，这样就隔离了多个线程对数据的数据共享。
- Spring的事务就借助了ThreadLocal类。

**Spring的事务借助ThreadLocal类获取 connection ：**
- Spring会从数据库连接池中获得一个connection，然会把connection放进ThreadLocal中，也就和线程绑定了，事务需要提交或者回滚，只要从ThreadLocal中拿到connection进行操作。
- 为何Spring的事务要借助ThreadLocal类？
    ```
    以JDBC为例，正常的事务代码可能如下：
    dbc = new DataBaseConnection();//第1行
    Connection con = dbc.getConnection();//第2行
    con.setAutoCommit(false);// //第3行
    con.executeUpdate(...);//第4行
    con.executeUpdate(...);//第5行
    con.executeUpdate(...);//第6行
    con.commit();////第7行
    上述代码，可以分成三个部分:
    事务准备阶段：第1～3行
    业务处理阶段：第4～6行
    事务提交阶段：第7行 
    ```
- 不管我们开启事务还是执行具体的sql都需要一个具体的数据库连接。
- 开发应用一般都采用三层结构，我们的Service会调用一系列的DAO对数据库进行多次操作，那么，这个时候我们就无法控制事务的边界了，因为实际应用当中，我们的Service调用的DAO的个数是不确定的，可根据需求而变化，而且还可能出现Service调用Service的情况。
- 如果不使用ThreadLocal，如何让三个DAO使用同一个数据源连接呢？我们就必须为每个DAO传递同一个数据库连接，要么就是在DAO实例化的时候作为构造方法的参数传递，要么在每个DAO的实例方法中作为方法的参数传递。
    ```
    Connection conn = getConnection();
    Dao1 dao1 = new Dao1(conn);
    dao1.exec();
    Dao2 dao2 = new Dao2(conn);
    dao2.exec();
    Dao3 dao3 = new Dao3(conn);
    dao3.exec();
    conn.commit();
    ```
- 为了让这个数据库连接可以跨阶段传递，又不显式的进行参数传递，就必须使用别的办法。
- Web容器中，每个完整的请求周期会由一个线程来处理。因此，如果我们能将一些参数绑定到线程的话，就可以实现在软件架构中跨层次的参数共享（是隐式的共享）。而JAVA中恰好提供了绑定的方法--使用ThreadLocal。
- 结合使用Spring里的IOC和AOP，就可以很好的解决这一点。
- 只要将一个数据库连接放入ThreadLocal中，当前线程执行时只要有使用数据库连接的地方就从ThreadLocal获得就行了。

**ThreadLocal的使用：**
- void set(Object value) 
  - 设置当前线程的线程局部变量的值。
- public Object get() 
  - 该方法返回当前线程所对应的线程局部变量。
- public void remove()
  - 将当前线程局部变量的值删除，目的是为了减少内存的占用，该方法是JDK 5.0新增的方法。
  - 需要指出的是，当线程结束后，对应该线程的局部变量将自动被垃圾回收，
  - 所以显式调用该方法清除线程的局部变量并不是必须的操作，但它可以加快内存回收的速度。
- protected Object initialValue()
  - 返回该线程局部变量的初始值，
  - 该方法是一个protected的方法，显然是为了让子类覆盖而设计的。
  - 这个方法是一个延迟调用方法，在线程第1次调用get()或set(Object)时才执行，并且仅执行1次。
  - ThreadLocal中的缺省实现直接返回一个null。
- public final static ThreadLocal<String> RESOURCE = new ThreadLocal<String>();
  - RESOURCE代表一个能够存放String类型的ThreadLocal对象。
  - 此时不论任何一个线程能够并发访问这个变量，对它进行写入、读取操作，都是线程安全的。

**代码实践： 使用 ThreadLocal 与 不使用 ThreadLocal**
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch2.UseThreadLocal](https://gitee.com/chentian114/chen_java_study)
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch2.NoUseThreadLocal](https://gitee.com/chentian114/chen_java_study)

**ThreadLocal实现解析**

![2.ThreadLocal原理](https://img-blog.csdnimg.cn/20210409190036826.png)

```
public class ThreadLocal<T> {
    //get方法，其实就是拿到每个线程独有的ThreadLocalMap
    //然后再用ThreadLocal的当前实例，拿到Map中的相应的Entry，然后就可以拿到相应的值返回出去。
    //如果Map为空，还会先进行map的创建，初始化等工作。
    public T get() {
        //先取到当前线程，然后调用getMap方法获取对应线程的ThreadLocalMap
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
    
    // Thread类中有一个 ThreadLocalMap 类型成员，所以getMap是直接返回Thread的成员
    ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }
    
    // ThreadLocalMap是ThreadLocal的静态内部类
    static class ThreadLocalMap {
        ThreadLocalMap(ThreadLocal<?> firstKey, Object firstValue) {
            // 用数组保存 Entry ， 因为可能有多个变量需要线程隔离访问，即声明多个 ThreadLocal 变量
            table = new Entry[INITIAL_CAPACITY];
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            // Entry 类似于 map 的 key-value 结构
            // key 就是 ThreadLocal， value 就是需要隔离访问的变量
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
            setThreshold(INITIAL_CAPACITY);
        }
        ...
    }
    
    //Entry内部静态类，它继承了WeakReference，
    //总之它记录了两个信息，一个是ThreadLocal<?>类型，一个是Object类型的值
    static class Entry extends WeakReference<ThreadLocal<?>> {
        /** The value associated with this ThreadLocal. */
        Object value;
    
        Entry(ThreadLocal<?> k, Object v) {
            super(k);
            value = v;
        }
    }
    
    //getEntry方法则是获取某个ThreadLocal对应的值
    private Entry getEntry(ThreadLocal<?> key) {
        int i = key.threadLocalHashCode & (table.length - 1);
        Entry e = table[i];
        if (e != null && e.get() == key)
            return e;
        else
            return getEntryAfterMiss(key, i, e);
    }
    
    //set方法就是更新或赋值相应的ThreadLocal对应的值
    private void set(ThreadLocal<?> key, Object value) {
        ...
    }
    ...
}

public class Thread implements Runnable {
    /* ThreadLocal values pertaining to this thread. This map is maintained
     * by the ThreadLocal class. */
    ThreadLocal.ThreadLocalMap threadLocals = null;

    ...
}
```    

**引用基础知识：**
- 引用：
  - 创建对象 Object o = new Object(); 
  - 这个o，我们可以称之为对象引用，而new Object()我们可以称之为在内存中产生了一个对象实例。
  - 当 o=null 时，只是表示o不再指向堆中Object的对象实例，不代表这个对象实例不存在了。
- 强引用:
  - 指在程序代码之中普遍存在的，类似“Object obj=new Object（）”
  - 这类的引用，只要强引用还存在，垃圾收集器永远不会回收掉被引用的对象实例。
- 软引用：
  - 用来描述一些还有用但并非必需的对象。
  - 对于软引用关联着的对象，在系统将要发生内存溢出异常之前，将会把这些对象实例列进回收范围之中进行第二次回收。如果这次回收还没有足够的内存，才会抛出内存溢出异常。
  - 在JDK 1.2之后，提供了SoftReference类来实现软引用。
- 弱引用：
  - 用来描述非必需对象的，但是它的强度比软引用更弱一些，被弱引用关联的对象实例只能生存到下一次垃圾收集发生之前。
  - 当垃圾收集器工作时，无论当前内存是否足够，都会回收掉只被弱引用关联的对象实例。
  - 在JDK 1.2之后，提供了WeakReference类来实现弱引用。
- 虚引用：
  - 也称为幽灵引用或者幻影引用，它是最弱的一种引用关系。
  - 一个对象实例是否有虚引用的存在，完全不会对其生存时间构成影响，也无法通过虚引用来取得一个对象实例。
  - 为一个对象设置虚引用关联的唯一目的就是能在这个对象实例被收集器回收时收到一个系统通知。
  - 在JDK 1.2之后，提供了PhantomReference类来实现虚引用。

**代码实践： 使用 ThreadLocal 引发内存泄漏**
- 准备：
  - 将堆内存大小设置为-Xmx256m
  - 启用一个线程池，大小固定为5个线程
  - 使用 jvisualvm 观察内存使用情况
-  参考代码：[com.chen.java.enjoyedu.concurrent.ch2.ThreadLocalOOM](https://gitee.com/chentian114/chen_java_study)
    ```
    //5M大小的数组
    private static class LocalVariable {
        private byte[] value = new byte[1024*1024*5];
    }
    
    // 创建线程池，固定为5个线程
    private static ThreadPoolExecutor poolExecutor
            = new ThreadPoolExecutor(5,5,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
            
    //ThreadLocal共享变量
    private ThreadLocal<LocalVariable> data;
    
    @Override
    public void run() {
        //场景1：不执行任何有意义的代码，当所有的任务提交执行完成后，查看内存占用情况，占用 25M 左右
        //System.out.println("hello ThreadLocal...");

        //场景2：创建 数据对象，执行完成后，查看内存占用情况，与场景1相同
        //new LocalVariable();

        //场景3：启用 ThreadLocal，执行完成后，查看内存占用情况，占用 100M 左右
        ThreadLocalOOM obj = new ThreadLocalOOM();
        obj.data = new ThreadLocal<>();
        obj.data.set(new LocalVariable());
        System.out.println("update ThreadLocal data value..........");

        //场景4： 加入 remove()，执行完成后，查看内存占用情况，与场景1相同
        //obj.data.remove();

        //分析：在场景3中，当启用了ThreadLocal以后确实发生了内存泄漏
    }
    ```
- 场景1：
  -  首先任务中不执行任何有意义的代码，当所有的任务提交执行完成后，可以看见，我们这个应用的内存占用基本上为25M左右
- 场景2：
  - 然后我们只简单的在每个任务中new出一个数组，执行完成后我们可以看见，内存占用基本和场景1相同
- 场景3：
  - 当我们启用了ThreadLocal以后，执行完成后我们可以看见，内存占用变为了100多M
- 场景4：
  - 我们加入一行代码 obj.data.remove(); ，再执行，看看内存情况,可以看见，内存占用基本和场景1相同。
- 场景分析：
  - 这就充分说明，场景3，当我们启用了ThreadLocal以后确实发生了内存泄漏。 
- 内存泄漏分析：
  - 通过对ThreadLocal的分析，我们可以知道每个Thread 维护一个 ThreadLocalMap，这个映射表的 key 是 ThreadLocal实例本身，value 是真正需要存储的 Object，也就是说 ThreadLocal 本身并不存储值，它只是作为一个 key 来让线程从 ThreadLocalMap 获取 value。
  - 仔细观察ThreadLocalMap，这个map是使用 ThreadLocal 的弱引用作为 Key 的，弱引用的对象在 GC 时会被回收。  
  ![2.ThreadLocal原理](https://img-blog.csdnimg.cn/20210409190036826.png)
  - 图中的虚线表示弱引用。
  - 当把threadlocal变量置为null以后，没有任何强引用指向threadlocal实例，所以threadlocal将会被gc回收。
  - 这样一来，ThreadLocalMap中就会出现key为null的Entry，就没有办法访问这些key为null的Entry的value，
  - 如果当前线程再迟迟不结束的话，这些key为null的Entry的value就会一直存在一条强引用链：Thread Ref -> Thread -> ThreaLocalMap -> Entry -> value，而这块value永远不会被访问到了，所以存在着内存泄露。
  - 可以通过Debug模式，查看变量 poolExecutor->workers->0->thread->threadLocals，会发现线程的成员变量 threadLocals 的 size=1，map 中存放了一个 referent=null, value=data对象 ；
  - 只有当前thread结束以后，current thread就不会存在栈中，强引用断开，Current Thread、Map value将全部被GC回收。
  - 最好的做法是在不需要使用ThreadLocal变量后，都调用它的remove()方法，清除数据。
- 场景3分析：
  - 在场景3中，虽然线程池里面的任务执行完毕了，但是线程池里面的5个线程会一直存在直到JVM退出，我们set了线程的localVariable变量后没有调用localVariable.remove()方法，导致线程池里面的5个线程的threadLocals变量里面的new LocalVariable()实例没有被释放。
- 从表面上看内存泄漏的根源在于使用了弱引用。为什么使用弱引用而不是强引用？下面我们分两种情况讨论：
  - key 使用强引用：对ThreadLocal对象实例的引用被置为null了，但是ThreadLocalMap还持有这个ThreadLocal对象实例的强引用，如果没有手动删除，ThreadLocal的对象实例不会被回收，导致Entry内存泄漏。 
  - key 使用弱引用：对ThreadLocal对象实例的引用被被置为null了，由于ThreadLocalMap持有ThreadLocal的弱引用，即使没有手动删除，ThreadLocal的对象实例也会被回收。value在下一次ThreadLocalMap调用set，get，remove都有机会被回收。
  - 比较两种情况，我们可以发现：由于ThreadLocalMap的生命周期跟Thread一样长，如果都没有手动删除对应key，都会导致内存泄漏，但是使用弱引用可以多一层保障。
- 因此，ThreadLocal内存泄漏的根源是：
  - 由于ThreadLocalMap的生命周期跟Thread一样长，如果没有手动删除对应key就会导致内存泄漏，而不是因为弱引用。
- 总结：
  - JVM利用设置ThreadLocalMap的Key为弱引用，来避免内存泄露。
  - JVM利用调用remove、get、set方法的时候，回收弱引用。
  - 当ThreadLocal存储很多Key为null的Entry的时候，而不再去调用remove、get、set方法，那么将导致内存泄漏。
  - 使用线程池 + ThreadLocal时要小心，因为这种情况下，线程是一直在不断的重复运行的，从而也就造成了value可能造成累积的情况。

**代码实践：错误使用ThreadLocal导致线程不安全：**
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch2.ThreadLocalUnsafe](https://gitee.com/chentian114/chen_java_study)
- 仔细考察ThreadLocal和Thead的代码，我们发现ThreadLocalMap中保存的其实是对象的一个引用，这样的话，当有其他线程对这个引用指向的对象实例做修改时，其实也同时影响了所有的线程持有的对象引用所指向的同一个对象实例。
- 这也就是为什么上面的程序为什么会输出一样的结果：5个线程中保存的是同一个Number对象的引用，因此它们最终输出的结果是相同的。
- 正确的用法是让每个线程中的ThreadLocal都应该持有一个新的Number对象。


## 线程间的协作

**线程间的协作：**
- 线程之间相互配合，完成某项工作；
- 比如一个线程修改了一个对象的值，而另一个线程感知到了变化，然后进行相应的操作；
- 前者是生产者，后者就是消费者，这种模式隔离了“做什么”（what）和“怎么做”（How）；
- 常见的方法是让消费者线程不断地循环检查变量是否符合预期在while循环中设置不满足的条件，如果条件满足则退出while循环，从而完成消费者的工作。
- 存在如下问题：
  -  1）难以确保及时性；
  -  2）难以降低开销。如果降低睡眠的时间，比如休眠1毫秒，这样消费者能更加迅速地发现条件变化，但是却可能消耗更多的处理器资源，造成了无端的浪费。

**等待和通知机制**
- 是指一个线程A调用了对象O的wait()方法进入等待状态，而另一个线程B调用了对象O的notify()或者notifyAll()方法，线程A收到通知后从对象O的wait()方法返回，进而执行后续操作。
- 上述两个线程通过对象O来完成交互，而对象上的wait()和notify/notifyAll()的关系就如同开关信号一样，用来完成等待方和通知方之间的交互工作。
- notify()：
  - 通知一个在对象上等待的线程,使其从wait方法返回,而返回的前提是该线程获取到了对象的锁，没有获得锁的线程重新进入WAITING状态。 
- notifyAll()：
  - 通知所有等待在该对象上的线程。
- wait()：
  - 调用该方法的线程进入 WAITING状态,只有等待另外线程的通知或被中断才会返回.需要注意,调用wait()方法后,会释放对象的锁。
- wait(long)：
  - 超时等待一段时间,这里的参数时间是毫秒,也就是等待长达n毫秒,如果没有通知就超时返回；
- wait (long,int)：
  - 对于超时时间更细粒度的控制,可以达到纳秒；

**等待和通知的标准范式：**
- 等待方遵循如下原则：
  - 1.获取对象的锁
  - 2.循环里判断条件是否满足，如果条件不满足，那么调用对象的wait()方法，被通知后仍要检查条件。
  - 条件满足则执行对应的逻辑。
  ```
  synchronized(对象){
      while(条件不满足){
          对象.wait();
      }
      对应的逻辑
  }
  ```
- 通知方遵循如下原则：
  - 1.获取对象的锁。
  - 2.改变条件。
  - 3.通知所有等待在对象上的线程。
  ```
  synchronized(对象){
      改变条件
      对象.notifyAll();
  }
  ```
- **在调用wait()、notify()系列方法之前，线程必须要获得该对象的对象级别锁，即只能在同步方法或同步块中调用wait() 方法、notify()系列方法；**
- 进入wait() 方法后，当前线程释放锁，在从wait() 返回前，线程与其他线程竞争重新获得锁，执行notify()系列方法的线程退出synchronized代码块的时候后，他们就会去竞争。
- 如果其中一个线程获得了该对象锁，它就会继续往下执行，在它退出synchronized代码块，释放锁后，其他的已经被唤醒的线程将会继续竞争获取该锁，一直进行下去，直到所有被唤醒的线程都执行完毕。

**notify() 和 notifyAll() 应该用谁？**
- 尽量用 notifyAll()
- 谨慎使用notify()，因为notify()只会唤醒一个线程，我们无法确保被唤醒的这个线程一定就是我们需要唤醒的线程; 

**代码实践：测试 notify() 和 notifyAll()**
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch2.TestWaitAndNotify](https://gitee.com/chentian114/chen_java_study)

**代码实践：测试 notify() 可能造成信号丢失**
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch2.TestNotifyAndNotifyAll](https://gitee.com/chentian114/chen_java_study)


**代码实践：使用 wait/notify 实现生产者消费者模式**
- 需求：采用多线程技术，例如wait/notify，设计实现一个符合生产者和消费者问题的程序，对某一个对象（枪膛）进行操作，其最大容量是20颗子弹，生产者线程是一个压入线程，它不断向枪膛中压入子弹，消费者线程是一个射出线程，它不断从枪膛中射出子弹。
请实现上面的程序。
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch2.GunWaitAndNotify](https://gitee.com/chentian114/chen_java_study)

**代码实践：等待超时模式实现一个连接池**
- 调用场景：
  - 调用一个方法时等待一段时间（一般来说是给定一个时间段），如果该方法能够在给定的时间段之内得到结果，那么将结果立刻返回，反之，超时返回默认结果。
- 假设等待时间段是T，那么可以推断出在当前时间now+T之后就会超时；
- 等待持续时间：REMAINING=T ；
- 超时时间：FUTURE=now+T ；
- 参考代码： 
  - [com.chen.java.enjoyedu.concurrent.ch2.DBPool](https://gitee.com/chentian114/chen_java_study)
  - [com.chen.java.enjoyedu.concurrent.ch2.DBPoolTest](https://gitee.com/chentian114/chen_java_study)
- 客户端获取连接的过程被设定为等待超时的模式，也就是在1000毫秒内如果无法获取到可用连接，将会返回给客户端一个null。
- 设定连接池的大小为10个，然后通过调节客户端的线程数来模拟无法获取连接的场景。
- 通过构造函数初始化连接的最大上限，通过一个双向队列来维护连接，调用方需要先调用fetchConnection(long)方法来指定在多少毫秒内超时获取连接，当连接使用完成后，需要调用releaseConnection(Connection)方法将连接放回线程池

**调用yield() 、sleep()、wait()、notify()等方法对锁有何影响？**
- yield() 、sleep()被调用后，都不会释放当前线程所持有的锁。
- 调用wait()方法后，会释放当前线程持有的锁，而且当前被唤醒后，会重新去竞争锁，锁竞争到后才会执行wait方法后面的代码。
- 调用notify()系列方法后，对锁无影响，线程只有在synchronized同步代码执行完后才会自然而然的释放锁，所以notify()系列方法一般都是synchronized同步代码的最后一行。

**join()方法**
- 线程A，执行了线程B的 join() 方法， 线程A必须要等待B执行完成以后，线程A才能继续执行；

代码实践： Join()方法的使用
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch2.UseJoin](https://gitee.com/chentian114/chen_java_study)

代码实践： 测试Sleep对锁的影响
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch2.SleepLock](https://gitee.com/chentian114/chen_java_study)


## 相关链接

gitee地址：[https://gitee.com/chentian114/chen_java_study](https://gitee.com/chentian114/chen_java_study)

github地址：[https://github.com/chentian114/chen_java_study](https://github.com/chentian114/chen_java_study)

CSDN地址：[https://blog.csdn.net/chentian114/category_10956933.html](https://blog.csdn.net/chentian114/category_10956933.html)

## 公众号

![知行chen](https://img-blog.csdnimg.cn/20201019220227866.jpg)

## 参考

享学 Java课程 Mark