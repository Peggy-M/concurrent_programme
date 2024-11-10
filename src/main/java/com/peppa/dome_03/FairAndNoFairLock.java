package com.peppa.dome_03;

/**
 * @Author: peppa
 * @Description: 公平锁 & 非公平锁
 * @Date: Created in 22:03 2024/10/14
 */
public class FairAndNoFairLock {
    /*
    公平锁: 按照顺序。线程 A 获取到了锁,线程 B 开始排队到线程 A 的后面,而此时线程 C 也来,只能排到线程 B 的后面,当线程 B 释放锁之后, 线程 C 才可以获取锁。* synchronized只能是非公平

    非公平锁: 线程 A 获取到锁，线程 B 没有获取到,线程 B 去排队, 此时线程 C 也来,当线程 A 的锁释放之后,线程 C 与 B 开始竞争,先尝试获取一波,如果获取到插队成功,否则得到 B 释放再次竞争。
    */
}
