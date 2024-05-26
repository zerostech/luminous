/*
 *
 *  * Copyright 2022. the original author or authors
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */
package com.zerostech.luminous.common.lock;

/**
 * Created on 2024/5/26.
 *
 * @author 迹_Jason
 */
public interface ILock {
    /**
     * try lock with fun.
     * 尝试加锁，加锁成功后执行对应的函数，执行完成自动解锁.
     * <p>
     * 推荐使用 {@link #tryLockWithFun(long waitMillSec, long leaseMillSec, VoidProcessFun fun)}
     *
     * @param fun 加锁成功后执行的函数
     */
    void tryLockWithFun(VoidProcessFun fun) throws Exception;

    /**
     * try lock with fun.
     * <p>
     * 推荐使用 {@link #tryLockWithFun(long waitMillSec, long leaseMillSec, VoidProcessFun fun)}
     *
     * @param waitMillSec 等待时间，单位毫秒
     * @param fun         加锁成功后执行的函数
     */
    void tryLockWithFun(long waitMillSec, VoidProcessFun fun) throws Exception;

    /**
     * try lock with fun.
     *
     * @param waitMillSec  等待毫秒数
     * @param leaseMillSec 锁释放毫秒数
     * @param fun          加锁成功后执行的函数
     */
    void tryLockWithFun(long waitMillSec, long leaseMillSec, VoidProcessFun fun) throws Exception;

    /**
     * try lock.
     * <p>
     * 推荐使用 {@link #tryLock(long waitMillSec, long leaseMillSec)}
     *
     * @return 是否加锁成功
     */
    Boolean tryLock();

    /**
     * try lock.
     * <p>
     * 推荐使用 {@link #tryLock(long waitMillSec, long leaseMillSec)}
     *
     * @param waitMillSec 等待毫秒数
     * @return 是否加锁成功
     */
    Boolean tryLock(long waitMillSec) throws InterruptedException;

    /**
     * try lock.
     *
     * @param waitMillSec  等待毫秒数
     * @param leaseMillSec 锁释放毫秒数，估计值，实际释放时间视各中间件的配置及执行环境会有一定偏差
     * @return 是否加锁成功
     */
    Boolean tryLock(long waitMillSec, long leaseMillSec) throws InterruptedException;

    /**
     * unlock.
     *
     * @return 是否解锁成功
     */
    Boolean unLock();

    /**
     * delete.
     * <p>
     * 谨慎使用
     */
    void delete();

    /**
     * is locked.
     *
     * @return true:有锁，false:没有锁
     */
    Boolean isLocked();
}
