package sys.bac.application.domain.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.device.DevicesResult;
import sys.bac.application.domain.results.device.JpaDevicesResult;
import sys.bac.application.port.in.device.GetDevicesUseCase;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class GetDevicesService implements GetDevicesUseCase {

    @Inject
    private DeviceRepository deviceRepo;

    @Override
    public DevicesResult findDevices(String query, int offset, int size) {
        JpaDevicesResult jpaResult = deviceRepo.getAllDevices(query, offset, size);
        LongResult totalResult = deviceRepo.count();
        
        DevicesResult result = new DevicesResult(new Page<>(jpaResult.getResult(), offset, size, totalResult.getResult()));
        if (jpaResult.hasError() || totalResult.hasError()) {
            result.setError(500, jpaResult.getMessage() + "\n" + totalResult.getMessage());
        }
        return result;
    }
}
