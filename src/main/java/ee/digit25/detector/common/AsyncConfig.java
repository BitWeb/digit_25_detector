package ee.digit25.detector.common;

import ee.digit25.detector.process.TransactionBatchProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    public static final String TRANSACTION_BATCH_THREAD_POOL_NAME = "transaction-batch-thread-pool";

    // Nr of max pending transactions / Nr of max requestable unverified transactions
    public static final int TRANSACTION_BATCH_THREAD_POOL_SIZE = 10000 / TransactionBatchProcessor.TRANSACTION_BATCH_SIZE;

    @Bean(TRANSACTION_BATCH_THREAD_POOL_NAME)
    public Executor transactionBatchThreadPool() {
        return AsyncConfig.createThreadPoolExecutor(TRANSACTION_BATCH_THREAD_POOL_SIZE);
    }

    public static Executor createThreadPoolExecutor(int poolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.initialize();

        return executor;
    }
}
