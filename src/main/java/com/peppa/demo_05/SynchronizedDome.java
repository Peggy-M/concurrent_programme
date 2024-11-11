package com.peppa.demo_05;

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