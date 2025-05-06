package ee.digit25.detector.domain.device.external;

import ee.bitweb.core.retrofit.RetrofitRequestExecutor;
import ee.digit25.detector.domain.device.external.api.Device;
import ee.digit25.detector.domain.device.external.api.DeviceApi;
import ee.digit25.detector.domain.device.external.api.DeviceApiProperties;
import ee.digit25.detector.domain.person.external.api.Person;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceRequester {

    private final DeviceApi api;
    private final DeviceApiProperties properties;

    ConcurrentHashMap<String , Device> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        int page = 0;

        while (true) {
            List<Device> devices = get(page, 1000);
            page +=1;
            if (devices.isEmpty()) {
                break;
            }
            for (Device device : devices) {
                cache.put(device.getMac(), device);
            }
        }
    }

    public Device get(String mac) {
        if (cache.containsKey(mac)) {
            return cache.get(mac);
        }
        log.info("Requesting device with mac({})", mac);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), mac));
    }

    public List<Device> get(List<String> macs) {
        log.info("Requesting devices with macs {}", macs);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), macs));
    }

    public List<Device> get(int pageNumber, int pageSize) {
        log.info("Requesting persons page {} of size {}", pageNumber, pageSize);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), pageNumber, pageSize));
    }
}
