

package com.zerostech.luminous.web.future;

import com.zerostech.luminous.common.future.IFutureExecutor;
import com.zerostech.luminous.web.future.FutureExecutor;
import com.zerostech.luminous.web.future.FutureExecutorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author hyz
 * 多线程异步执行器自动配置
 * @since 2023/7/20
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {FutureExecutorProperties.class})
public class FutureExecutorAutoConfiguration {

    private final Map<String, ThreadPoolExecutor> executorContainer;
    private final FutureExecutorProperties futureExecutorProperties;

    /**
     * 异步执行器
     *
     * @return {@link IFutureExecutor}
     */
    @Bean
    public IFutureExecutor futureExecutor() {
        // 如果没有配置线程池，则使用默认线程池
        // 必然存在一个线程池，key为default
        if (executorContainer.isEmpty() || !executorContainer.containsKey(IFutureExecutor.DEFAULT)) {
            log.warn("No thread pool configuration is found, the default thread pool will be used");
            executorContainer.put(IFutureExecutor.DEFAULT, new ThreadPoolExecutor(5, 10, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy()));
        }
        if (futureExecutorProperties.getExecutors() == null) {
            return new FutureExecutor(executorContainer);
        }
        // 初始化线程池，已存在的线程池会被覆盖
        futureExecutorProperties.getExecutors().forEach((key, value) -> {
            ThreadPoolExecutor threadPoolExecutor = executorContainer.get(key);
            if (threadPoolExecutor == null) {
                try {
                    threadPoolExecutor = new ThreadPoolExecutor(value.getCorePoolSize(), value.getMaxPoolSize(), value.getKeepAliveTime(), value.getTimeUnit(), getRunnableBlockingQueue(value), getThreadFactory(value), value.getHandler().newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("Initial thread pool failure:", e);
                    throw new RuntimeException(e);
                }
                executorContainer.put(key, threadPoolExecutor);
            } else {
                if (value.getCorePoolSize() != null) {
                    threadPoolExecutor.setCorePoolSize(value.getCorePoolSize());
                }
                if (value.getMaxPoolSize() != null) {
                    threadPoolExecutor.setMaximumPoolSize(value.getMaxPoolSize());
                }
                if (value.getKeepAliveTime() != null) {
                    threadPoolExecutor.setKeepAliveTime(value.getKeepAliveTime(), value.getTimeUnit());
                }
                if (value.getHandler() != null) {
                    try {
                        threadPoolExecutor.setRejectedExecutionHandler(value.getHandler().newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        log.error("Initial thread pool failure:", e);
                        throw new RuntimeException(e);
                    }
                }
                if (value.getThreadFactory() != null) {
                    try {
                        threadPoolExecutor.setThreadFactory(value.getThreadFactory().newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        log.error("Initial thread pool failure:", e);
                        throw new RuntimeException(e);
                    }
                }
            }

        });
        return new FutureExecutor(executorContainer);
    }

    /**
     * 获取可运行阻塞队列
     *
     * @param value 价值
     * @return {@link BlockingQueue}<{@link Runnable}>
     * @throws InstantiationException 实例化异常
     * @throws IllegalAccessException 非法访问异常
     */
    private BlockingQueue<Runnable> getRunnableBlockingQueue(FutureExecutorProperties.ExecutorConfig value) throws InstantiationException, IllegalAccessException {
        BlockingQueue<Runnable> blockingQueue;
        if (value.getBlockingQueue() == null) {
            blockingQueue = new LinkedBlockingQueue<>();
        } else {
            blockingQueue = value.getBlockingQueue().newInstance();
        }
        return blockingQueue;
    }

    /**
     * 获取线工厂
     *
     * @param value 价值
     * @return {@link ThreadFactory}
     * @throws InstantiationException 实例化异常
     * @throws IllegalAccessException 非法访问异常
     */
    private ThreadFactory getThreadFactory(FutureExecutorProperties.ExecutorConfig value) throws InstantiationException, IllegalAccessException {
        ThreadFactory threadFactory;
        if (value.getThreadFactory() == null) {
            threadFactory = Executors.defaultThreadFactory();
        } else {
            threadFactory = value.getThreadFactory().newInstance();
        }
        return threadFactory;
    }


}
