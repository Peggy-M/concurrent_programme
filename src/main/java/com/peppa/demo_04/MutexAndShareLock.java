package com.peppa.demo_04;

/**
 * @Author: peppa
 * @Description: 互斥与共享锁
 * @Date: Created in 22:12 2024/10/14
 */
public class MutexAndShareLock {

    /*
     * 互斥锁:
     *    - 同一时间点,只有有一个线程持有或独占该锁,如果线程 A 持有了该锁, 线程 B 只能等到该锁的释放; [synchronized、ReentrantLock,都是属于互斥锁]
     * 共享锁:
     *    - 同一时间点,可以有多个线程共同持有该锁,该锁可以被多个线程共享使用;[ReentrantReadWriteLock 既可以互斥也可以共享]
     *
     * 对于共享锁存在的价值其实本身是在于 对于数据的读写, 将设我需要写数据,为了保证数据的唯一性,可以使用 互斥锁, 但如果仅仅只是读操作对数据本身不做出修改,使用共享锁将会更高效,因此也就是有了 ReentrantReadWriteLock 读的时候是共享,写的人时候是互斥
     *
     */

}
