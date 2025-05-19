package ee.digit25.detector.process;

import ee.digit25.detector.domain.transaction.TransactionValidator;
import ee.digit25.detector.domain.transaction.external.api.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * This processor is responsible for processing of a single transaction
 */
public class TransactionProcessor {

    private final TransactionValidator validator;

    public Result process(Transaction transaction) {
        try {
            if (validator.isLegitimate(transaction)) {
                return new Result(transaction, true);
            }
        } catch (Exception e) {
            log.info("Transaction processing failed", e);
        }

        return new Result(transaction, false);
    }

    @Getter
    @AllArgsConstructor
    public static class Result {
        private Transaction transaction;
        private boolean valid;
    }
}
