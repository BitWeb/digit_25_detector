package ee.digit25.detector.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("TRANSACTION_EXECUTOR_THREAD_POOL")
    public Executor getAsyncExecutor() {
        return AsyncConfig.createThreadPoolExecutor(1000);
    }

    @Bean("PROCESS_EXECUTOR_THREAD_POOL")
    public Executor getAsyncProcessExecutor() {
        return AsyncConfig.createThreadPoolExecutor(50);
    }

    public static Executor createThreadPoolExecutor(int poolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.initialize();

        return executor;
    }
}
