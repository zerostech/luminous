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
package com.zerostech.luminous.common.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2024/5/26.
 *
 * @author 迹_Jason
 */
public interface ICache {
    /**
     * key is exists.
     *
     * @param key key
     * @return 是否存在
     */
    boolean exists(String key);

    /**
     * get String value..
     *
     * @param key key
     * @return 值
     */
    String get(String key);

    /**
     * set value.
     *
     * @param key   key
     * @param value value
     */
    void set(String key, String value);

    /**
     * set value.
     *
     * @param key   key
     * @param value value
     */
    void setIfAbsent(String key, String value);

    /**
     * set value with expire time.
     *
     * @param key       key
     * @param value     value
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void setex(String key, String value, long expireSec);

    /**
     * set value with expire time.
     *
     * @param key       key
     * @param value     value
     * @param expireSec 过期时间(seconds)，0表示永不过期
     * @return true 设置成功 , false 设置失败（key已存在）
     */
    Boolean setnx(String key, String value, long expireSec);


    /**
     * get and set value.
     *
     * @param key   key
     * @param value value
     * @return 旧值
     */
    String getSet(String key, String value);

    /**
     * delete key.
     *
     * @param key key
     */
    void del(String key);

    /**
     * add list.
     *
     * @param key   key
     * @param value value
     */
    void lpush(String key, String value);

    /**
     * add list.
     *
     * @param key       key
     * @param values    values
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void lmset(String key, List<String> values, long expireSec);

    /**
     * add list.
     *
     * @param key    key
     * @param values values
     */
    void lmset(String key, List<String> values);

    /**
     * get list value.
     * <p>
     * 注意，Redis的列表是栈结构，先进后出
     *
     * @param key key
     * @return 栈顶的列表值
     */
    String lpop(String key);

    /**
     * get list length.
     *
     * @param key key
     * @return 长度
     */
    Long llen(String key);

    /**
     * get all values in the list.
     *
     * @param key key
     * @return 值列表
     */
    List<String> lget(String key);

    /**
     * add set collection.
     *
     * @param key    key
     * @param values values
     */
    void smset(String key, List<String> values);

    /**
     * add set collection.
     *
     * @param key       key
     * @param values    values
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void smset(String key, List<String> values, long expireSec);

    /**
     * set Set collection.
     *
     * @param key   key
     * @param value value
     */
    void sset(String key, String value);

    /**
     * get and remove the top value of the stack.
     *
     * @param key key
     * @return 返回值
     */
    String spop(String key);

    /**
     * get the length of the set.
     *
     * @param key key
     * @return 长度
     */
    Long slen(String key);

    /**
     * delete values in the set.
     *
     * @param key    key
     * @param values values
     * @return 影响的行数
     */
    Long sdel(String key, String... values);

    /**
     * @param key key
     * @return 值集合
     */
    Set<String> sget(String key);

    /**
     * set Hash collection.
     *
     * @param key       key
     * @param items     items
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void hmset(String key, Map<String, String> items, long expireSec);

    /**
     * set Hash collection.
     *
     * @param key   key
     * @param items items
     */
    void hmset(String key, Map<String, String> items);


    /**
     * set Hash collection field value.
     *
     * @param key   key
     * @param field field
     * @param value value
     */
    void hset(String key, String field, String value);

    /**
     * set Hash collection field value.
     *
     * @param key   key
     * @param field field
     * @param value value
     */
    void hsetIfAbsent(String key, String field, String value);


    /**
     * get Hash collection field value.
     *
     * @param key   key
     * @param field field
     * @return field对应的value，不存在时返回null
     */
    String hget(String key, String field);

    /**
     * get all items in the Hash collection.
     *
     * @param key key
     * @return 所有items
     */
    Map<String, String> hgetAll(String key);

    /**
     * check if the field exists in the Hash collection.
     *
     * @param key   key
     * @param field field
     * @return 是否存在
     */
    boolean hexists(String key, String field);

    /**
     * get all keys in the Hash collection.
     *
     * @param key key
     * @return 所有keys
     */
    Set<String> hkeys(String key);

    /**
     * get all values in the Hash collection.
     *
     * @param key key
     * @return 所有values
     */
    Set<String> hvalues(String key);

    /**
     * get the length of the Hash collection.
     *
     * @param key key
     * @return 长度
     */
    Long hlen(String key);

    /**
     * delete field in the Hash collection.
     *
     * @param key   key
     * @param field field
     */
    void hdel(String key, String field);

    /**
     * increase value with atomic operation.
     *
     * @param key       key，key不存在时会自动创建值为0的对象
     * @param incrValue 要增加的值，必须是Long Int Float 或 Double
     * @return 操作后的值
     */
    Long incrBy(String key, long incrValue);

    /**
     * add ZSet collection.
     *
     * @param h         h
     * @param hk        hk
     * @param incrValue 要增加的值，必须是Long Int Float 或 Double
     * @return 操作后的值
     */
    Long hashIncrBy(String h, String hk, long incrValue);


    /**
     * decrease value with atomic operation.
     *
     * @param key       key不存在时会自动创建值为0的对象
     * @param decrValue 要减少的值，必须是Long  或 Int
     * @return 操作后的值
     */
    Long decrBy(String key, long decrValue);

    /**
     * decrease value with atomic operation.
     *
     * @param h         h
     * @param hk        hk
     * @param decrValue 要减少的值，必须是Long  或 Int
     * @return 操作后的值
     */
    Long hashDecrBy(String h, String hk, long decrValue);

    /**
     * set expire time.
     *
     * @param key       key
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void expire(String key, long expireSec);

    /**
     * get expire time by seconds.
     *
     * @param key key
     * @return -2 key不存在，-1 对应的key永不过期，正数 过期时间(seconds)
     */
    Long ttl(String key);

    /**
     * 删除当前数据库中的所有Key.
     */
    void flushdb();

    /**
     * set bit.
     *
     * @param key    key
     * @param offset offset
     * @param value  值
     * @return 原来的值
     */
    Boolean setBit(String key, long offset, boolean value);

    /**
     * get bit.
     *
     * @param key    key
     * @param offset offset
     * @return 指定偏移的值
     */
    Boolean getBit(String key, long offset);
}
