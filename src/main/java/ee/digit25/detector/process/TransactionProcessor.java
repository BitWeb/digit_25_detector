package ee.digit25.detector.process;

import ee.digit25.detector.domain.transaction.TransactionValidator;
import ee.digit25.detector.domain.transaction.external.TransactionRequester;
import ee.digit25.detector.domain.transaction.external.TransactionVerifier;
import ee.digit25.detector.domain.transaction.external.api.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProcessor {

    private final TransactionValidator validator;
    private final TransactionVerifier verifier;

    @Async("TRANSACTION_EXECUTOR_THREAD_POOL")
    public CompletableFuture<Void> process(Transaction transaction) {
        try {
            if (validator.isLegitimate(transaction)) {
                verify(transaction);
            } else {
                reject(transaction);
            }
        } catch (Exception e) {
            log.info("Transaction processing failed", e);
        }

        return CompletableFuture.completedFuture(null);
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
