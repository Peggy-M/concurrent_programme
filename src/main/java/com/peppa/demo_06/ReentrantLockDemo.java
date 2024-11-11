package com.peppa.demo_06;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: peppa
 * @Description: ReentrantLock 的使用实例
 * @Date: Created in 0:23 2024/11/12
 */
public class ReentrantLockDemo {
    private final static ReentrantLock lock = new ReentrantLock();
    private static final long oldTime = new Date().getTime();


    public static void main(String[] args) {
        Thread t1 = new Thread(ReentrantLockDemo::fun_1);
        Thread t2 = new Thread(ReentrantLockDemo::fun_1);
        t1.setName("Thread_one");
        t2.setName("Thread_two");
        t1.start();
        t2.start();
    }

    public static void fun_1() {
        lock.lock();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long newTime = new Date().getTime();
        System.out.println("current Thread -" + Thread.currentThread().getName() + "- call function,run time is :" + (newTime - oldTime) + "ms");
        lock.unlock();
    }
}
