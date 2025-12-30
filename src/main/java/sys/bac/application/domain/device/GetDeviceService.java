package sys.bac.application.domain.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.port.in.device.GetDeviceByIdUseCase;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class GetDeviceService implements GetDeviceByIdUseCase {

    @Inject
    private DeviceRepository deviceRepo;

    @Override
    public DeviceResult loadDeviceById(LongId id) {
        return deviceRepo.getDeviceById(id);
    }
}

