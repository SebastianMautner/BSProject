package sys.bac.application.domain.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.port.in.device.DeleteDeviceUseCase;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class DeleteDeviceService implements DeleteDeviceUseCase {

    @Inject
    private DeviceRepository deviceRepo;

    @Override
    public NoContentResult deleteDevice(LongId id) {
        return deviceRepo.delete(id);
    }
}

