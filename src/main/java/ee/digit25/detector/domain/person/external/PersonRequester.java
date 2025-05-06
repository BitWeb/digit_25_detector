package ee.digit25.detector.domain.person.external;

import ee.bitweb.core.retrofit.RetrofitRequestExecutor;
import ee.digit25.detector.domain.person.external.api.Person;
import ee.digit25.detector.domain.person.external.api.PersonApi;
import ee.digit25.detector.domain.person.external.api.PersonApiProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonRequester {

    private final PersonApi api;
    private final PersonApiProperties properties;

    ConcurrentHashMap<String , Person> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        int page = 0;

        while (true) {
            List<Person> persons = get(page, 1000);
            page +=1;
            if (persons.isEmpty()) {
                break;
            }
            for (Person person : persons) {
                cache.put(person.getPersonCode(), person);
            }
        }
    }

    public Person get(String personCode) {
        if (cache.containsKey(personCode)) {
            return cache.get(personCode);
        }
        log.info("Requesting person with personCode {}", personCode);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), personCode));
    }

    public List<Person> get(List<String> personCodes) {
        log.info("Requesting persons with personCodes {}", personCodes);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), personCodes));
    }

    public List<Person> get(int pageNumber, int pageSize) {
        log.info("Requesting persons page {} of size {}", pageNumber, pageSize);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), pageNumber, pageSize));
    }
}
