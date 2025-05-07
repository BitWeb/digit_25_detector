package ee.digit25.detector.process;

import ee.digit25.detector.domain.transaction.TransactionValidator;
import ee.digit25.detector.domain.transaction.external.TransactionRequester;
import ee.digit25.detector.domain.transaction.external.TransactionVerifier;
import ee.digit25.detector.domain.transaction.external.api.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class Processor {

    private final int TRANSACTION_BATCH_SIZE = 10000/25;
    private final TransactionRequester requester;
    private final TransactionValidator validator;
    private final TransactionVerifier verifier;

    private final TransactionProcessor processor;

    @Async("PROCESS_EXECUTOR_THREAD_POOL")
    public CompletableFuture<Void> processTransactions() throws ExecutionException, InterruptedException {
        log.info("Starting to process a batch of transactions of size {}", TRANSACTION_BATCH_SIZE);
        List<Transaction> transactions = requester.getUnverified(TRANSACTION_BATCH_SIZE);

        List<CompletableFuture<TransactionProcessor.Result>> futures = new ArrayList<>();
        for (Transaction transaction : transactions) {
            futures.add(processor.process(transaction));
        }
        List<Transaction> validTransactions = new ArrayList<>();
        List<Transaction> invalidTransactions = new ArrayList<>();

        for (CompletableFuture<TransactionProcessor.Result> future : futures) {
            TransactionProcessor.Result result = future.get();
            if (result.isValid()) {
                validTransactions.add(result.getTransaction());
            } else {
                invalidTransactions.add(result.getTransaction());
            }
        }

        if (!validTransactions.isEmpty()) {
            verifier.verify(validTransactions);
        }
        if (!invalidTransactions.isEmpty()) {
            verifier.reject(invalidTransactions);
        }

        return CompletableFuture.completedFuture(null);
    }

    //@Scheduled(fixedDelay = 1000) //Runs every 1000 ms after the last run
    public void process() {
        log.info("Starting to process a batch of transactions of size {}", TRANSACTION_BATCH_SIZE);

        List<Transaction> transactions = requester.getUnverified(TRANSACTION_BATCH_SIZE);

        for (Transaction transaction : transactions) {
            try {
                process(transaction);
            } catch (Exception e) {
                log.error("Error processing transaction {}", transaction, e);
            }
        }
    }

    private void process(Transaction transaction) {
        if (validator.isLegitimate(transaction)) {
            verify(transaction);
        } else {
            reject(transaction);
        }
    }

    private void reject(Transaction transaction) {
        log.info("Not legitimate transaction {}", transaction.getId());
        verifier.reject(transaction);
    }

    private void verify(Transaction transaction) {
        log.info("Legitimate transaction {}", transaction.getId());
        verifier.verify(transaction);
    }
}
