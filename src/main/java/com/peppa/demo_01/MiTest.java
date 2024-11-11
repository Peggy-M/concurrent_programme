package com.peppa.demo_01;

/**
 * @Author: peppa
 * @Description: 重入锁 & 不可以重入锁
 * @Date: Created in 21:16 2024/10/14
 */
public class MiTest {

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
