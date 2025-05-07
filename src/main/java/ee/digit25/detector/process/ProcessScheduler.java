package ee.digit25.detector.process;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class ProcessScheduler {

    private final Processor processor;

    @Scheduled(fixedDelay = 1000)
    public void process() throws ExecutionException, InterruptedException {
        while(true) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                futures.add(processor.processTransactions());
            }

            for (CompletableFuture<Void> future : futures) {
                future.get();
            }
            Thread.sleep(10);
        }
    }
}
