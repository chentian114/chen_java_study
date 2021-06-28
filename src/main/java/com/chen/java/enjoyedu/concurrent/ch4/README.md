# 并发编程之原子操作CAS

[toc]

## 原子操作CAS

**原子性：**
- 访问（读、写）某个共享变量的操作从其执行线程以外的任何线程来看，该操作要么已经执行结束要么尚未发生，即其他线程不会看到该操作执行了部分的中间效果。
- 保证原子性的方法：使用锁和CAS指令。它们能够保障一个共享变量在任意一个时刻只能够被一个线程访问。

**什么是原子操作？**
- 指不会被线程调度机制打断的操作；这种操作一旦开始，就一直运行到结束，中间不会有任何 context switch （切换到另一个线程）。
- 实现原子操作可以使用锁和 CAS指令 ；

**使用基于阻塞的锁机制实现原子操作：**
- 当一个线程拥有锁的时候，访问同一资源的其它线程需要等待，直到该线程释放锁；
- 问题：
  - 被阻塞的线程优先级很高；
  - 获得锁的线程一直不释放锁；
  - 有大量的线程来竞争资源，那CPU将会花费大量的时间和资源来处理这些竞争，同时，还有可能出现一些例如死锁之类的情况；
- 锁机制是一种比较粗糙，粒度比较大的机制；

**CAS(Compare And Swap)指令：**
- Compare And Swap ,指令级别保证这是一个原子操作；
- 每一个CAS操作过程都包含三个运算符：一个内存地址V，一个期望的值A和一个新值B，
- 基本思路：
  - 操作的时候如果这个地址上存放的值等于这个期望的值A，则将地址上的值赋为新值B，否则不做任何操作；
  - 循环（死循环，自旋）里不断的进行CAS操作。

![4.CAS原理](https://img-blog.csdnimg.cn/20210417151307802.png)

**CAS是怎么实现线程的安全呢？**
- 语言层面不做处理，将其交给硬件—CPU和内存，
- 利用CPU的多处理能力，实现硬件层面的阻塞，再加上volatile变量的特性即可实现基于原子操作的线程安全。


## CAS实现原子操作的三大问题

**ABA问题：**
- 问题：
  - 如果一个值原来是A，变成了B，又变成了A，那么使用CAS进行检查时会发现它的值没有发生变化，但是实际上却变化了。
- 解决思路：
  - ABA问题的解决思路就是使用版本号。 
  - 在变量前面追加上版本号，每次变量更新的时候把版本号加1，那么A→B→A就会变成1A→2B→3A。

**循环时间长开销大**
- 自旋CAS如果长时间不成功，会给CPU带来非常大的执行开销。

**只能保证一个共享变量的原子操作**
- 当对一个共享变量执行操作时，我们可以使用循环CAS的方式来保证原子操作，但是对多个共享变量操作时，循环CAS就无法保证操作的原子性，可以通过：
  - 1.使用锁。
  - 2.原子引用；从Java 1.5开始，JDK提供了AtomicReference类来保证引用对象之间的原子性，就可以把多个变量放在一个对象里来进行CAS操作。

## JDK中相关原子操作类的使用

### 基本类型

**AtomicInteger:**
- int addAndGet（int delta）：以原子方式将输入的数值与实例中的值（AtomicInteger里的value）相加，并返回结果。
- boolean compareAndSet（int expect，int update）：如果输入的数值等于预期值，则以原子方式将该值设置为输入的值。
- int getAndIncrement()：以原子方式将当前值加1，注意，这里返回的是自增前的值。
- int getAndSet（int newValue）：以原子方式设置为newValue的值，并返回旧值。

**代码实践：使用AtomicInteger：**
- 参考代码：[com.chen.java.enjoyedu.concurrent.ch4.UseAtomicInt](https://gitee.com/chentian114/chen_java_study)

**AtomicIntegerArray:**
- 主要是提供原子的方式更新数组里的整型，其常用方法如下。
- int addAndGet（int i，int delta）：以原子方式将输入值与数组中索引i的元素相加。
- boolean compareAndSet（int i，int expect，int update）：如果当前值等于预期值，则以原子方式将数组位置i的元素设置成update值。
- 需要注意的是，数组value通过构造方法传递进去，然后AtomicIntegerArray会将当前数组复制一份，所以当AtomicIntegerArray对内部的数组元素进行修改时，不会影响传入的数组。

**代码实践：使用 AtomicIntegerArray**
- [com.chen.java.enjoyedu.concurrent.ch4.UseAtomicIntegerArray](https://gitee.com/chentian114/chen_java_study)

### 更新引用类型

原子更新基本类型的AtomicInteger，只能更新一个变量，如果要原子更新多个变量，就需要使用这个原子更新引用类型提供的类。Atomic包提供了以下3个类。

**AtomicReference:**
- 原子更新引用类型。

**代码实践：使用 AtomicReference:**
- 参考代码： [com.chen.java.enjoyedu.concurrent.ch4.UseAtomicReference](https://gitee.com/chentian114/chen_java_study)


**AtomicStampedReference:**
- 利用版本戳的形式记录了每次改变以后的版本号，这样的话就不会存在ABA问题了。
- AtomicStampedReference是使用pair的int stamp作为计数器使用；
- 解决ABA问题，版本使用 整型，可确定被改动过几次；


**AtomicMarkableReference:**
- 原子更新带有标记位的引用类型。可以原子更新一个布尔类型的标记位和引用类型。
- 构造方法是AtomicMarkableReference（V initialRef，boolean initialMark）。
- 解决ABA问题，版本使用 boolean ，可确定是否有被改动过；
- AtomicMarkableReference 的pair使用的是boolean mark。

AtomicStampedReference可能关心的是动过几次，AtomicMarkableReference关心的是有没有被人动过，方法都比较简单。

**代码实践： 使用 AtomicStampedReference:**
- 参考代码： [com.chen.java.enjoyedu.concurrent.ch4.UseAtomicStampedReference](https://gitee.com/chentian114/chen_java_study)


### 原子更新字段类

如果需原子地更新某个类里的某个字段时，就需要使用原子更新字段类，Atomic包提供了以下3个类进行原子字段更新。
- AtomicIntegerFieldUpdater
  - 原子更新整型的字段的更新器。
- AtomicLongFieldUpdater
  - 原子更新长整型字段的更新器。
- AtomicReferenceFieldUpdater
  - 原子更新引用类型里的字段。 

**要想原子地更新字段类需要两步：**
- 第一步，因为原子更新字段类都是抽象类，每次使用的时候必须使用静态方法newUpdater()创建一个更新器，并且需要设置想要更新的类和属性。
- 第二步，更新类的字段（属性）必须使用public volatile修饰符。

## 实践

有一个残缺A类实现了线程安全的 get方法和compareAndSet()方法，自行实现它的递增方法

- 参考代码： [com.chen.java.enjoyedu.concurrent.ch4.HalfAtomicInt](https://gitee.com/chentian114/chen_java_study)

## 相关链接

gitee地址：[https://gitee.com/chentian114/chen_java_study](https://gitee.com/chentian114/chen_java_study)

github地址：[https://github.com/chentian114/chen_java_study](https://github.com/chentian114/chen_java_study)

CSDN地址：[https://blog.csdn.net/chentian114/category_10956933.html](https://blog.csdn.net/chentian114/category_10956933.html)

## 公众号

![知行chen](https://img-blog.csdnimg.cn/20201019220227866.jpg)

## 参考

享学 Java课程 Mark

https://baike.baidu.com/item/%E5%8E%9F%E5%AD%90%E6%93%8D%E4%BD%9C