package com.zerostech.luminous.web.future;

import com.alibaba.ttl.TtlRunnable;
import com.zerostech.luminous.common.exception.ExceptionCode;
import com.zerostech.luminous.common.exception.LuminousBizException;
import com.zerostech.luminous.common.future.IFutureExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author hyz
 * @since 2023/7/20
 */
@Slf4j
public class FutureExecutor implements IFutureExecutor {

    private final Map<String, ThreadPoolExecutor> executorContainer;

    public FutureExecutor(Map<String, ThreadPoolExecutor> executorContainer) {
        this.executorContainer = executorContainer;
    }


    /**
     * 异步执行返回list聚合
     *
     * @param list     循环list
     * @param function 执行逻辑
     * @param <T>      执行类型
     */
    public <T, R> List<R> executeReturnList(List<T> list, Function<T, R> function, String key) {

        List<R> resultList = new CopyOnWriteArrayList<>();
        CompletableFuture<Void> allCf = CompletableFuture.allOf(list.stream().filter(Objects::nonNull).map(data -> CompletableFuture.runAsync(TtlRunnable.get(() -> {
            R result = function.apply(data);
            resultList.add(result);
        }), executorContainer.get(key)).exceptionally(e -> {
            log.warn("future executor apply error", e);
            return null;
        })).toArray(CompletableFuture[]::new));

        allCf.join();
        return resultList;
    }

    /**
     * 异步执行返回list聚合
     *
     * @param list       循环list
     * @param function   执行逻辑
     * @param key        线程池key
     * @param resultList resultList
     * @param <T>        执行类型
     */
    public <T, R> CompletableFuture<Void> executeReturnCf(List<T> list, Function<T, R> function, String key, List<R> resultList) {
        return CompletableFuture.allOf(list.stream().filter(Objects::nonNull).map(data -> CompletableFuture.runAsync(TtlRunnable.get(() -> {
            R result = function.apply(data);
            resultList.add(result);
        }), executorContainer.get(key)).exceptionally(e -> {
            log.warn("future executor apply error", e);
            return null;
        })).toArray(CompletableFuture[]::new));
    }

    @Override
    public <T, R> List<R> executeReturnList(List<T> list, Function<T, R> function) {
        return executeReturnList(list, function, DEFAULT);
    }

    /**
     * 异步执行无返回
     *
     * @param list     循环list
     * @param consumer 执行逻辑
     * @param <T>      执行类型
     */
    public <T> void executeVoid(List<T> list, Consumer<T> consumer, String key) {
        CompletableFuture<Void> allCf = executeVoidReturnCf(list, consumer, key);
        allCf.join();
    }

    @Override
    public <T> void executeVoid(List<T> list, Consumer<T> consumer) {
        executeVoid(list, consumer, DEFAULT);
    }

    /**
     * 异步执行无返回
     *
     * @param list     循环list
     * @param consumer 执行逻辑
     * @param <T>      执行类型
     */
    public <T> CompletableFuture<Void> executeVoidReturnCf(List<T> list, Consumer<T> consumer, String key) {

        return CompletableFuture.allOf(list.stream().filter(Objects::nonNull).map(data -> CompletableFuture.runAsync(() -> {
            try {
                consumer.accept(data);
            } catch (LuminousBizException e) {
                log.warn("future executor apply error", e);
            }
        }, executorContainer.get(key))).toArray(CompletableFuture[]::new));
    }

    @Override
    public <T> CompletableFuture<Void> executeVoidReturnCf(List<T> list, Consumer<T> consumer) {
        return executeVoidReturnCf(list, consumer, DEFAULT);
    }


    /**
     * 异步执行返回list聚合
     *
     * @param list     循环list
     * @param function 执行逻辑
     * @param <T>      执行类型
     */
    public <T> Map<String, Object> executeReturnMap(List<T> list, Function<T, Object> function, Function<T, String> getMapKey, Boolean ifPutError, String key) {

        Map<String, Object> map = new ConcurrentHashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        CompletableFuture<Void> allCf = CompletableFuture.allOf(list.stream().filter(Objects::nonNull).map(data -> CompletableFuture.supplyAsync(() -> {
            try {
                Object obj = function.apply(data);
                if (Objects.nonNull(obj)) {
                    map.put(getMapKey.apply(data), obj);
                }
            } catch (LuminousBizException e) {
                log.warn("future executor apply error", e);
                if (ifPutError) {
                    map.put(getMapKey.apply(data), e.getMessage());
                }
            }
            return null;
        }, executorContainer.get(key))).toArray(CompletableFuture[]::new));

        allCf.join();
        return map;
    }

    @Override
    public <T> Map<String, Object> executeReturnMap(List<T> list, Function<T, Object> function, Function<T, String> getMapKey, Boolean ifPutError) {
        return executeReturnMap(list, function, getMapKey, ifPutError, DEFAULT);
    }

    /**
     * 异步执行返回错误信息map
     *
     * @param list    循环list
     * @param consume 执行逻辑
     * @param <T>     执行类型
     */
    public <T> Map<String, String> consumeReturnErrorMap(List<T> list, Consumer<T> consume, BiFunction<T, String, Map<String, String>> buildMapFunction, String key) {

        Map<String, String> map = new ConcurrentHashMap<>();
        CompletableFuture<Void> allCf = CompletableFuture.allOf(list.stream().filter(Objects::nonNull).map(data -> CompletableFuture.runAsync(TtlRunnable.get(() -> {
            try {
                consume.accept(data);
            } catch (LuminousBizException e) {
                log.warn("future executor apply error", e);
                map.putAll(buildMapFunction.apply(data, e.getMessage()));
            }
        }), executorContainer.get(key))).toArray(CompletableFuture[]::new));

        allCf.join();
        return map;
    }

    @Override
    public <T> Map<String, String> consumeReturnErrorMap(List<T> list, Consumer<T> consume, BiFunction<T, String, Map<String, String>> buildMapFunction) {
        return consumeReturnErrorMap(list, consume, buildMapFunction, DEFAULT);
    }

    @Override
    public CompletableFuture<Object> valid(String key, Runnable... runnableS) {

        CompletableFuture<?>[] cfArray = Arrays.stream(runnableS).filter(Objects::nonNull).map(runnable -> CompletableFuture.runAsync(runnable, executorContainer.get(key))).toArray(CompletableFuture[]::new);

        CompletableFuture<Void> allCf = CompletableFuture.allOf(cfArray);

        CompletableFuture<Void> expCf = new CompletableFuture<>();

        Stream.of(cfArray).forEach(cf -> cf.exceptionally(t -> {
            expCf.completeExceptionally(t);
            return null;
        }));

        return CompletableFuture.anyOf(allCf, expCf).exceptionally(e -> {
            throw new LuminousBizException(ExceptionCode.PROCESS_CHECK_DATA_UN_MATCH, e.getCause().getMessage());
        });

    }

    @Override
    public CompletableFuture<Object> valid(Runnable... runnableS) {
        return valid(DEFAULT, runnableS);
    }

    @Override
    public Map<String, ?> execute(String key, Map<String, Supplier<?>> supplierMap) {

        if (MapUtils.isEmpty(supplierMap)) {
            return null;
        }

        Map<String, CompletableFuture<?>> map = new ConcurrentHashMap<>();

        supplyAsync(key, supplierMap, map);

        Map<String, CompletableFuture<?>> futureMap = this.executeCfSupplier(map);
        Map<String, Object> resultMap = new HashMap<>();
        if (futureMap != null) {
            futureMap.forEach((k, cf) -> resultMap.put(k, cf.join()));
        }

        return resultMap;
    }

    /**
     * 供应异步
     *
     * @param key         键
     * @param supplierMap 供应商字典
     * @param map         字典
     */
    private void supplyAsync(String key, Map<String, Supplier<?>> supplierMap, Map<String, CompletableFuture<?>> map) {
        supplierMap.forEach((k, supplier) -> {
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(supplier, executorContainer.get(key)).exceptionally(e -> {
                log.warn("future executor supplier :{}, exp:", supplier, e);
                return null;
            });
            map.put(k, cf);
        });
    }

    @Override
    public Map<String, ?> execute(Map<String, Supplier<?>> supplierMap) {
        return execute(DEFAULT, supplierMap);
    }

    private Map<String, CompletableFuture<?>> executeCfSupplier(Map<String, CompletableFuture<?>> cfMap) {

        if (MapUtils.isEmpty(cfMap)) {
            return null;
        }

        List<CompletableFuture<?>> list = new CopyOnWriteArrayList<>();

        cfMap.forEach((k, future) -> {
            future.exceptionally(e -> {
                log.warn("future executor :{}, exp:", future, e);
                return null;
            });
            list.add(future);
        });

        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return cfMap;
    }

    @Override
    public Map<String, CompletableFuture<?>> executeCf(String key, Map<String, Supplier<?>> supplierMap) {

        Map<String, CompletableFuture<?>> map = new ConcurrentHashMap<>();

        if (MapUtils.isEmpty(supplierMap)) {
            return map;
        }
        supplyAsync(key, supplierMap, map);

        return map;
    }

    @Override
    public Map<String, CompletableFuture<?>> executeCf(Map<String, Supplier<?>> supplierMap) {
        return executeCf(DEFAULT, supplierMap);
    }

    @Override
    public <T, R> Map<String, CompletableFuture<R>> executeReturnCompletableFuture(List<T> list, Function<T, R> function, Function<T, String> getMapKey, String key) {
        Map<String, CompletableFuture<R>> map = new ConcurrentHashMap<>();

        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        list.stream().filter(Objects::nonNull).forEach(data -> map.put(getMapKey.apply(data), CompletableFuture.supplyAsync(() -> function.apply(data), executorContainer.get(key)).exceptionally(e -> {
            log.warn("future executor executeReturnCompletableFuture apply error, data:{}", data, e);
            return null;
        })));
        return map;
    }

    @Override
    public <T, R> Map<String, CompletableFuture<R>> executeReturnCompletableFuture(List<T> list, Function<T, R> function, Function<T, String> getMapKey) {
        return executeReturnCompletableFuture(list, function, getMapKey, DEFAULT);
    }

    @Override
    public <T> CompletableFuture<T> executeSupplier(String key, Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executorContainer.get(key)).exceptionally(e -> {
            log.warn("future executor executeSupplier get error, supplier:{}", supplier, e);
            return null;
        });
    }

    @Override
    public <T> CompletableFuture<T> executeSupplier(Supplier<T> supplier) {
        return executeSupplier(DEFAULT, supplier);
    }

    @Override
    public CompletableFuture<Void> executeRunnable(Runnable runnable, String key) {
        return CompletableFuture.runAsync(TtlRunnable.get(runnable), executorContainer.get(key)).exceptionally(e -> {
            log.warn("future executor executeSupplier get error, runnable:{}", runnable, e);
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> executeRunnable(Runnable runnable) {
        return executeRunnable(runnable, DEFAULT);
    }
}
