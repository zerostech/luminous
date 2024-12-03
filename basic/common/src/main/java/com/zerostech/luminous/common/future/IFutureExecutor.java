package com.zerostech.luminous.common.future;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author hyz
 * @since 2023/7/20
 */
public interface IFutureExecutor {

    String DEFAULT = "default";

    <T> void executeVoid(List<T> list, Consumer<T> consumer, String key);

    <T> void executeVoid(List<T> list, Consumer<T> consumer);

    <T> CompletableFuture<Void> executeVoidReturnCf(List<T> list, Consumer<T> consumer, String key);

    <T> CompletableFuture<Void> executeVoidReturnCf(List<T> list, Consumer<T> consumer);

    <T, R> List<R> executeReturnList(List<T> list, Function<T, R> function, String key);

    <T, R> CompletableFuture<Void> executeReturnCf(List<T> list, Function<T, R> function, String key, List<R> resultList);

    <T, R> List<R> executeReturnList(List<T> list, Function<T, R> function);

    <T> Map<String, Object> executeReturnMap(List<T> list, Function<T, Object> function, Function<T, String> getMapKey, Boolean ifPutError, String key);

    <T> Map<String, Object> executeReturnMap(List<T> list, Function<T, Object> function, Function<T, String> getMapKey, Boolean ifPutError);

    <T> Map<String, String> consumeReturnErrorMap(List<T> list, Consumer<T> consume, BiFunction<T, String, Map<String, String>> buildMapFunction, String key);

    <T> Map<String, String> consumeReturnErrorMap(List<T> list, Consumer<T> consume, BiFunction<T, String, Map<String, String>> buildMapFunction);

    CompletableFuture<Object> valid(String key, Runnable... runnableS);

    CompletableFuture<Object> valid(Runnable... runnableS);

    Map<String, ?> execute(String key, Map<String, Supplier<?>> supplierMap);

    Map<String, ?> execute(Map<String, Supplier<?>> supplierMap);

    Map<String, CompletableFuture<?>> executeCf(String key, Map<String, Supplier<?>> supplierMap);

    Map<String, CompletableFuture<?>> executeCf(Map<String, Supplier<?>> supplierMap);

    <T, R> Map<String, CompletableFuture<R>> executeReturnCompletableFuture(List<T> list, Function<T, R> function, Function<T, String> getMapKey, String key);

    <T, R> Map<String, CompletableFuture<R>> executeReturnCompletableFuture(List<T> list, Function<T, R> function, Function<T, String> getMapKey);

    <T> CompletableFuture<T> executeSupplier(String key, Supplier<T> supplier);

    <T> CompletableFuture<T> executeSupplier(Supplier<T> supplier);

    CompletableFuture<Void> executeRunnable(Runnable runnable, String key);

    CompletableFuture<Void> executeRunnable(Runnable runnable);
}
