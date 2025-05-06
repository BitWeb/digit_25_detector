package ee.digit25.detector.domain.account.external;

import ee.bitweb.core.retrofit.RetrofitRequestExecutor;
import ee.digit25.detector.domain.account.external.api.Account;
import ee.digit25.detector.domain.account.external.api.AccountApi;
import ee.digit25.detector.domain.account.external.api.AccountApiProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRequester {

    private final AccountApi api;
    private final AccountApiProperties properties;

    ConcurrentHashMap<String , Account> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        int page = 0;

        while (true) {
            List<Account> accounts = get(page, 1000);
            page +=1;
            if (accounts.isEmpty()) {
                break;
            }
            for (Account person : accounts) {
                cache.put(person.getNumber(), person);
            }
        }
    }

    public Account get(String accountNumber) {
        if (cache.containsKey(accountNumber)) {
            return cache.get(accountNumber);
        }

        log.info("Requesting account {}", accountNumber);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), accountNumber));
    }

    public List<Account> get(List<String> numbers) {
        log.info("Requesting accounts with numbers {}", numbers);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), numbers));
    }

    public List<Account> get(int pageNumber, int pageSize) {
        log.info("Requesting accounts page {} of size {}", pageNumber, pageSize);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), pageNumber, pageSize));
    }
}
