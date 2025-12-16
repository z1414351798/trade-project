package com.example.trade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class TradeWorkerPoolConfig {

    @Bean("tradeWorkerExecutor")
    public ExecutorService tradeWorkerExecutor() {

        return new ThreadPoolExecutor(
                16,                     // core pool
                32,                     // max pool
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20000),   // queue for backpressure
                new ThreadFactory() {
                    private final AtomicInteger idx = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "trade-worker-" + idx.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()  // backpressure handler
        );
    }

    @Bean("derivedTradeWorkerExecutor")
    public ExecutorService derivedTradeWorkerExecutor() {

        return new ThreadPoolExecutor(
                16,                     // core pool
                32,                     // max pool
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20000),   // queue for backpressure
                new ThreadFactory() {
                    private final AtomicInteger idx = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "derived-trade-worker-" + idx.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()  // backpressure handler
        );
    }

    @Bean("caWorkerExecutor")
    public ExecutorService caWorkerExecutor() {

        return new ThreadPoolExecutor(
                16,                     // core pool
                32,                     // max pool
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20000),   // queue for backpressure
                new ThreadFactory() {
                    private final AtomicInteger idx = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "ca-worker-" + idx.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()  // backpressure handler
        );
    }
}
