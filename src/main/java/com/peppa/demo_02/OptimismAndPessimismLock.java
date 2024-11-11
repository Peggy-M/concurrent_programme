package com.peppa.demo_02;

/**
 * @Author: peppa
 * @Description: 乐观锁与悲观锁
 * @Date: Created in 21:59 2024/10/14
 */
public class OptimismAndPessimismLock {
    //悲观锁-获取不到锁资源时，会将当前线程挂起（进入BLOCKED、WAITING）,线程得挂起涉及到用户态&内核态的切换,这种切换比较消耗计算机资源
    //用户态: JVM 可以自行执行的指令，不需要借助操作系统执行
    //内核态: JVM 不可以自行执行，需要操作系统才可以执行

}
