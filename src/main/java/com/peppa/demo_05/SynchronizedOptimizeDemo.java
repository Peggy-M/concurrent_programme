package com.peppa.demo_05;

/**
 * @Author: peppa
 * @Description: Synchronized 的锁优化
 * @Date: Created in 22:33 2024/10/14
 */
public class SynchronizedOptimizeDemo {
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