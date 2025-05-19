package ee.digit25.detector.process;

import ee.digit25.detector.common.AsyncConfig;
import ee.digit25.detector.domain.transaction.external.TransactionRequester;
import ee.digit25.detector.domain.transaction.external.TransactionVerifier;
import ee.digit25.detector.domain.transaction.external.api.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
/**
 * This class is responsible for processing of a batch of transactions.
 */
public class TransactionBatchProcessor {

    public static final int TRANSACTION_BATCH_SIZE = 1000;

    private final TransactionRequester requester;
    private final TransactionVerifier verifier;

    private final TransactionProcessor processor;

    @Async(AsyncConfig.TRANSACTION_BATCH_THREAD_POOL_NAME)
    public CompletableFuture<Void> processTransactions() throws ExecutionException, InterruptedException {
        log.info("Starting to process a batch of transactions of size {}", TRANSACTION_BATCH_SIZE);
        List<Transaction> transactions = requester.getUnverified(TRANSACTION_BATCH_SIZE);

        List<Transaction> validTransactions = new ArrayList<>();
        List<Transaction> invalidTransactions = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionProcessor.Result result = processor.process(transaction);
            if (result.isValid()) {
                validTransactions.add(result.getTransaction());
            } else {
                invalidTransactions.add(result.getTransaction());
            }
        }

        ArrayList<CompletableFuture<Void>> verification = new ArrayList<>();

        if (!validTransactions.isEmpty()) {
            verification.add(verifier.verify(validTransactions));
        }
        if (!invalidTransactions.isEmpty()) {
            verification.add(verifier.reject(invalidTransactions));
        }

        // Wait for both requests to finish before declaring job done
        for (CompletableFuture<Void> future : verification) {
            future.get();
        }

        return CompletableFuture.completedFuture(null);
    }

}
