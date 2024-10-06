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

import com.zerostech.luminous.common.id.Ids;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis-ids 管理器
 *
 * @author 迹_Jason
 * @date 2024/10/06
 */
@Slf4j
public class RedisIds implements Ids {

    private static final String DEFAULT_IP = "127.0.0.1";
    private static final ConcurrentHashMap<String, IdGenerator> idCache = new ConcurrentHashMap<>();
    private static String hostIp;
    private static final Long DEFAULT_SERVICE_ID = 0L;
    private static final Integer DEFAULT_WORKER_ID = 1;

    private final IdsProperties idsProperties;

    public RedisIds(IdsProperties idsProperties) {
        this.idsProperties = idsProperties;
    }

    /**
     * 获取id管理器
     *
     * @param serviceId 服务id
     * @return 返回id管理器
     */
    private IdGenerator getIdManage(long serviceId) {
        Integer workId = Objects.nonNull(idsProperties.getWorkId()) ? idsProperties.getWorkId() : DEFAULT_WORKER_ID;
        String key = getKey(serviceId, workId);

        IdGenerator idGenerator = idCache.get(key);
        if (idGenerator == null) {
            synchronized (idCache) {
                idGenerator = idCache.get(key);
                if (idGenerator == null) {
                    idGenerator = new IdGenerator(serviceId, workId);
                    idCache.put(key, idGenerator);
                }
            }
        }
        return idGenerator;
    }

    /**
     * 通过业务id随机生成id
     *
     * @param serviceId
     * @return 服务Ida
     */
    @Override
    public Long getId(long serviceId) {
        return getIdManage(serviceId).nextId();
    }

    /**
     * 随机生成id
     */
    @Override
    public Long getId() {
        return getIdManage(DEFAULT_SERVICE_ID).nextId();
    }

    /**
     * 获取key，用来查询map中是否已存在IdGenerator对象
     *
     * @param serviceId 服务id
     * @param workerId  机器id
     * @return 返回key
     */
    public static String getKey(long serviceId, long workerId) {
        return serviceId + "_" + workerId;
    }


    public static long getWorkerId() {
        String ip = getHostIp();
        final byte[] md5 = DigestUtils.md5(ip.getBytes());
        return ((0x000000FF & (long) md5[md5.length - 1])
                | (0x0000FF00 & ((long) md5[md5.length - 2] << 8))) >> 8;
    }

    /**
     * 获取当前主机ip地址
     *
     * @return ip地址
     */
    public static String getHostIp() {
        if (hostIp != null) {
            return hostIp;
        }

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while (nias.hasMoreElements()) {
                    InetAddress ia = nias.nextElement();
                    if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
                        hostIp = ia.getHostAddress();
                        return hostIp;
                    }
                }
            }
        } catch (SocketException e) {
            log.error(e.getMessage(), e);
        }
        hostIp = DEFAULT_IP;
        return hostIp;
    }

}
