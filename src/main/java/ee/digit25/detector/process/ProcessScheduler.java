package ee.digit25.detector.process;

import ee.digit25.detector.common.AsyncConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
/**
 *  This class is mainly responsible for initialization and management of transaction verification
 */
public class ProcessScheduler {

    private final TransactionBatchProcessor processor;

    @Scheduled(fixedDelay = 1000) // We will use cron only as a means to restart process.
    public void process() throws ExecutionException, InterruptedException {
        while(true) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (int i = 0; i < AsyncConfig.TRANSACTION_BATCH_THREAD_POOL_SIZE; i++) {
                futures.add(processor.processTransactions());
            }
            // Wait for all the threads to finish work before starting a new batch.
            // Such waiting is not ideal as some threads will sleep while other threads to work.
            for (CompletableFuture<Void> future : futures) {
                future.get();
            }
            Thread.sleep(1); // Small pause to keep the main thread resting (Otherwise running a risk of crashing cpu).
        }
    }
}
