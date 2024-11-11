# 多线程与高并发

## 锁

### 锁-锁的分类

#### 可重入与不可重入

~~~ java
/**
 * @Author: peppa
 * @Description: 重入锁 & 不可以重入锁
 * @Date: Created in 21:16 2024/10/14
 */
public class MiTest {

    // 当前线程获取到A锁，在获取之后尝试再次获取A锁是可以直接拿到的。
    private static volatile MiTest miTest;

    public MiTest() {
    }
   
    public static MiTest getInstance() {
        if (miTest == null) {
            synchronized (MiTest.class) { // 01 - 这里获取到了锁
                if (miTest == null) {
                    synchronized (MiTest.class) { // 02 - 再次重新获得锁
                        miTest = new MiTest();
                    }
                }
            } // 03  - 这个位置释放锁
        }
        return miTest;
    }

}
~~~



~~~ java
// 当前线程获取到A锁，在获取之后尝试再次获取A锁，无法获取到的，因为A锁被当前线程占用着，需要等待自己释放锁再获取锁


~~~



#### 乐观锁与悲观锁





#### 公平锁与非公平锁





#### 互斥锁与共享锁



### 深入 Synchronized

#### 类锁、对象锁

~~~ java
/**
 * @Author: peppa
 * @Description: Synchronized 的使用
 * @Date: Created in 22:22 2024/10/14
 */
public class SynchronizedDome {
    // synchronized 的锁是基于对象实现的, 一般当中使用 同步代码块 或 同步方式

    public static void main(String[] args) {
        // 锁的是,当前 Test.class
        Test.a();

        Test test = new Test();
        // 锁的是new出来的test对象
        test.b();

        //由于两个 synchronized 的同步方法块所占用的锁对象不同分别是 Test.class 类 与 test 对象,因此不具有互斥性都可以被执行
    }
}

//类锁
class Test{
    public static synchronized void a(){
        System.out.println("1111");
    }

    public synchronized void b(){
        System.out.println("2222");
    }
}

class Test2{
    //对象锁
    public synchronized void method(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("3333");
    }
}
~~~

#### synchronized 的优化

> 由于在 JDK1.5 当中 Doug Lee 推出了 ReentrantLock ,lock 锁的性能远远高于了当时版本的 synchronized 锁,因此 JDK 团队在 JDK1.6 的时候对 synchronized 做了大量的优化
> 优化: 锁消除、锁膨胀、锁升级 [ 无锁=> 匿名偏向=> 偏向锁=> 轻量级锁=> 重量级锁]

~~~ java
/**
 * @Author: peppa
 * @Description: Synchronized 的锁优化
 * @Date: Created in 22:33 2024/10/14
 */
public class SynchronizedOptimizeDome {
    /*
    由于在 JDK1.5 当中 Doug Lee 推出了 ReentrantLock ,lock 锁的性能远远高于了当时版本的 synchronized 锁,因此 JDK 团队在 JDK1.6 的时候对 synchronized 做了大量的优化
    优化: 锁消除、锁膨胀、锁升级 [ 无锁=> 匿名偏向=> 偏向锁=> 轻量级锁=> 重量级锁]
     */

    //================================锁消除====================================
    //此时这个方法的当中未触发任何的临界资源,因此 synchronized 这个方法可以默认就没有的
    public synchronized void method1() {
        //这里没有操作任何的临界资源[临界资源-共享变量、文件、需要线程安全访问的资源]
    }

    //================================锁膨胀====================================
    //下面的代码可以发现在 01 处获取锁资源 ,在 02 处释放锁资源,一共需要频繁的获取释放循环 99999 次,此时在 JVM 允许期间就会对其优化将 method2 优化成 method3 的形式,将锁从循环中取出放置到循环外
    //案例: method2
    public void method2() {
        for (int i = 0; i < 999999; i++) {
            //在这里需要循环 99999 次
            synchronized (this) {   // 01 - 获取锁资源

            }                       // 02 - 释放锁资源
        }
    }

    //案例: method3
    public void method3() {
        // 这是上面的代码会触发锁膨胀
        synchronized (this) {      // 01 - 获取锁资源
            for (int i = 0; i < 999999; i++) {

            }
        }                          // 02 - 释放锁资源
    }

    //================================锁升级====================================
    // ReentrantLock 的实现，是先基于乐观锁的 CAS 尝试获取锁资源，如果拿不到锁资源，才会挂起线程,synchronized在JDK1.6之前，完全就是获取不到锁，立即挂起当前线程，所以synchronized性能比较差。
    /*
        关于锁升级有以下几种过程状态:
            1. 首先, 无锁、匿名偏向状态,当前的对象只是一个单纯未作为锁对象的对象.
            2. 偏向锁: 如果当前的锁资源,只有一个线程在频繁的获取与释放,此时有一个线程过来,只需要判断,当前指向的线程是否当前的线程
                如果是 - 则直接获取到当前的线程锁;
                否则 - 基于 CAS 的方式进行比较,尝试将偏向锁指向当前的线程,如果获取不到,触发锁升级,升级为轻量级锁 [偏向锁状态出现了竞争的情况]
            3.轻量级锁: 会采用自旋锁的方式频繁的以 CAS 的形式获取锁资源 [采用 自适应自旋锁]
                如果获取成功,拿到锁资源
                如果自旋到一定的次数,没有拿到锁资源,锁升级

            4.重量级锁: 就是最传统的 synchronization 方式,拿不到锁资源,就挂起当前的线程 [用户态 切换 内核态]
     */

}
~~~

#### synchronized  的实现原理

![image-20241014231138439](https://peggy-note.oss-cn-hangzhou.aliyuncs.com/english/image-20241014231138439.png)

![image-20241014231440402](https://peggy-note.oss-cn-hangzhou.aliyuncs.com/english/image-20241014231440402.png)

在 MarkWord 头信息当中标记了四种状态 [无锁、偏向锁、轻量级锁、重量级锁]

#### synchronized 锁升级

为了可以在Java中看到对象头的 MarkWord 信息，需要导入依赖

~~~ xml
<dependency>
    <groupId>org.openjdk.jol</groupId>
    <artifactId>jol-core</artifactId>
    <version>0.9</version>
</dependency>
~~~

~~~ java
/**
 * @Author: peppa
 * @Description: 锁升级的过程
 * @Date: Created in 23:17 2024/10/14
 */
public class SynchronizedUpgradeDome {
    public static void main(String[] args) throws InterruptedException {

        Thread.sleep(1000);
        Object lock = new Object();
        String printable = ClassLayout.parseInstance(lock).toPrintable();
        /*
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         * 低位 00000001 无锁状态
         *
         * java.lang.Object object internals:
         *   OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *        0     4        (object header)                           05 00 00 00 (00000101 00000000 00000000 00000000) (5)
         *        4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *        8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
         *       12     4        (loss due to the next object alignment)
         *  Instance size: 16 bytes
         *  Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         * 低位 00000101 00000000 00000000 00000000 匿名偏向
         *
         */
        System.out.println(printable);

        new Thread(() -> {
            /*
             * T1:java.lang.Object object internals:
             *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
             *       0     4        (object header)                           05 00 00 00 (00000101 00000000 00000000 00000000) (5)
             *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
             *       8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
             *      12     4        (loss due to the next object alignment)
             * Instance size: 16 bytes
             * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
             * 低位 00000101 00000000 00000000 00000000 匿名偏向
             */
            synchronized (lock) {
                try {
//                    Thread.sleep(10);       // 时间 CAS 足够短 T2 升级-轻量级
                    Thread.sleep(1000); // 时间 CAS 足够长 比较次数多升级 - 重量级
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //偏向锁
                System.out.println("T1:" + ClassLayout.parseInstance(lock).toPrintable());
            }
        }).start();

        /*
         * T2:java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           b0 f7 3f 0d (10110000 11110111 00111111 00001101) (222295984)
         *       4     4        (object header)                           44 00 00 00 (01000100 00000000 00000000 00000000) (68)
         *       8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         * 低位 10110000 11110111 00111111 00001101 轻量级锁
         */


        /*
         * T2:java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           2a 1d 9e c7 (00101010 00011101 10011110 11000111) (-945939158)
         *       4     4        (object header)                           92 02 00 00 (10010010 00000010 00000000 00000000) (658)
         *       8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         * 低位 00101010 00011101 10011110 11000111 重量级锁
         */
        Thread.sleep(20);
        //main -  偏向锁 - 轻量级锁 CAS - 重量级锁
        synchronized (lock) {
            System.out.println("T2:" + ClassLayout.parseInstance(lock).toPrintable());
        }
    }
}
~~~

整个锁在升级的过程

![image-20241014235230865](https://peggy-note.oss-cn-hangzhou.aliyuncs.com/english/image-20241014235230865.png)

Lock Record 以及 ObjectMonitor 存储的内容

![image-20241014235309834](https://peggy-note.oss-cn-hangzhou.aliyuncs.com/english/image-20241014235309834.png)

 #### hotSpot 的核心属性源码

![image-20241111225425906](https://peggy-note.oss-cn-hangzhou.aliyuncs.com/english/image-20241111225425906.png)

![image-20241111230556744](https://peggy-note.oss-cn-hangzhou.aliyuncs.com/english/image-20241111230556744.png)

![image-20241111231653513](https://peggy-note.oss-cn-hangzhou.aliyuncs.com/english/image-20241111231653513.png)

~~~ cpp
void ATTR ObjectMonitor::enter(TRAPS) {
  Thread * const Self = THREAD ; //获取到当前线程
  void * cur ;

  cur = Atomic::cmpxchg_ptr (Self, &_owner, NULL) ; // CAS 竞争获取锁
  if (cur == NULL) { //获取锁成功
     return ;
  }

  if (cur == Self) { //所有获取的锁等于当前线程,锁重入操作
     // TODO-FIXME: check for integer overflow!  BUGID 6557169.
     _recursions ++ ; //锁重入次数 +1
     return ;
  }

  if (Self->is_lock_owned ((address)cur)) { //轻量级升级重量级锁
    assert (_recursions == 0, "internal state error");
    _recursions = 1 ; //锁重入设置 1
    _owner = Self ; //持有锁为当前线程
    OwnerIsThread = 1 ; //设置是否为当前线程的标志位
    return ;
  }
  Atomic::inc_ptr(&_count);//没有获取到锁,此时竞争锁的线程个数+1

  EventJavaMonitorEnter event;

  {
    OSThreadContendState osts(Self->osthread());
    ThreadBlockInVM tbivm(jt);

    Self->set_current_pending_monitor(this);

    // TODO-FIXME: change the following for(;;) loop to straight-line code.
    for (;;) {
      jt->set_suspend_equivalent();
      EnterI (THREAD) ; //将当前竞争的线程添加到队列当中添加到 _cxq 队列当中
      if (!ExitSuspendEquivalent(jt)) break ;
          _recursions = 0 ;
      _succ = NULL ;
      exit (false, Self) ;

      jt->java_suspend_self();
    }
    Self->set_current_pending_monitor(NULL);
  }

}

~~~

### 深入 ReentrantLock

`ReentantLock` 与 `synchronized` 的底层都是基于 JVM 层面实现的,两者的实现原理存在差异，ReentrantLock 本身是基于 AQS 实现,而 synchronized 的底层是调用了 C++

的 ObjectMonitor 方法, synchronized 的锁升级过程是不可逆向的，这就表示一旦升级成重量级锁就不会降级，这样往往会消耗硬件的资源，所以在锁竞争比较激烈的情况下推荐使用 ReentantLock 是处理。由于 ReentantLock 在 java 层面封装了一些功能性的方法，因此其功能也更加的丰富。

#### AQS 介绍

既然 ReentrantLock 是基于 AQS 实现的, 那首先必定需要先知道什么是 AQS ，以及 AQS 有什么具体的作用。AQS 是抽象基类 `AbstractQueuedSynchronizer` 的简写，是并发大师 Doug Lea 编写，比如 `ReentantLock` 、`ThreadPoolExecutor` 、`Semaphore`都实现了 AQS .

![image-20241112001937347](https://peggy-note.oss-cn-hangzhou.aliyuncs.com/english/image-20241112001937347.png)

#### 源码解析

