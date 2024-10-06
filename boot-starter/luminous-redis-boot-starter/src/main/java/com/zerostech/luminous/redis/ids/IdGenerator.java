/*
 *
 *  *
 *  *  Copyright 2024. the original author or authors
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  *
 *
 */
package com.zerostech.luminous.redis.ids;


import java.util.Random;

/**
 * id生成器
 *
 * @author 迹_Jason
 * @date 2024/10/06
 */
public class IdGenerator {

    /**
     * 业务线标识id所占的位数
     **/
    private final long serviceIdBits = 6L;
    /**
     * 业务线标识支持的最大数据标识id(这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxServiceId = -1L ^ (-1L << serviceIdBits);
    private final long serviceId;


    /**
     * 机器id所占的位数
     **/
    private final long workerIdBits = 10L;
    /**
     * 支持的最大机器id
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long workerId;

    /**
     * 序列在id中占的位数
     **/
    private final long sequenceBits = 7L;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);


    /**
     * 开始时间戳（2017年7月21日）
     **/
    private final long twepoch = 1500566400000L;
    /**
     * 最后一次的时间戳
     **/
    private volatile long lastTimestamp = -1L;
    /**
     * 毫秒内序列
     **/
    private volatile long sequence = 0L;
    /**
     * 随机生成器
     **/
    private static final Random random = new Random();


    /**
     * 机器id左移位数
     **/
    private final long workerIdShift = sequenceBits;
    /**
     * 业务线id左移位数
     **/
    private final long serviceIdShift = workerIdBits + sequenceBits;
    /**
     * 时间戳左移位数
     **/
    private final long timestampLeftShift = serviceIdBits + workerIdBits + sequenceBits;

    public IdGenerator(final long serviceId, final long workerId) {
        if ((serviceId > maxServiceId) || (serviceId < 0)) {
            throw new IllegalArgumentException(String.format("service Id can't be greater than %d or less than 0", maxServiceId));
        }
        if ((workerId > maxWorkerId) || (workerId < 0)) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.serviceId = serviceId;
        this.workerId = workerId;
    }

    public synchronized long nextId() throws IdsException {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new IdsException("Clock moved backwards.  Refusing to generate id for " + (
                    lastTimestamp - timestamp) + " milliseconds.");
        }
        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //跨毫秒时，序列号总是归0，会导致序列号为0的ID比较多，导致生成的ID取模后不均匀，所以采用10以内的随机数
            sequence = random.nextInt(10) & sequenceMask;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (serviceId << serviceIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     */
    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 获取序号
     *
     * @param id 生成的id
     * @return 返回序号
     */
    public static Long getSequence(final Long id) {
        final String str = Long.toBinaryString(id);
        final int size = str.length();
        final String sequenceBinary = str.substring(size - 7, size);
        return Long.parseLong(sequenceBinary, 2);
    }

    /**
     * 获取机器
     *
     * @param id 生成的id
     * @return 返回机器号
     */
    public static Long getWorker(final Long id) {
        final String str = Long.toBinaryString(id);
        final int size = str.length();
        final String sequenceBinary = str.substring(size - 7 - 10, size - 7);
        return Long.parseLong(sequenceBinary, 2);
    }

    /**
     * 获取业务线
     *
     * @param id 生成的id
     * @return 返回业务线id
     */
    public static Long getService(final Long id) {
        final String str = Long.toBinaryString(id);
        final int size = str.length();
        final String sequenceBinary = str.substring(size - 7 - 10 - 8, size - 7 - 10);
        return Long.parseLong(sequenceBinary, 2);
    }

}
