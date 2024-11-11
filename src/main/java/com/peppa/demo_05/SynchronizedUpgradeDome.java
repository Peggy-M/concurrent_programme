package com.peppa.demo_05;

import org.openjdk.jol.info.ClassLayout;

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
