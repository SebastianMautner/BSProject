package sys.bac.application.domain.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.port.in.device.DeleteDeviceUseCase;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class DeleteDeviceService implements DeleteDeviceUseCase {

    @Inject
    private DeviceRepository deviceRepo;

    @Override
    public NoContentResult deleteDevice(LongId id) {
        DeviceResult exists = deviceRepo.getDeviceById(id);
        NoContentResult result = new NoContentResult();
        if (exists.isEmpty()) {
            result.setError(404, "NotFound");
        } else if (exists.hasError()) {
            result.setError(500, exists.getMessage());
        } else {
            result = deviceRepo.delete(id);
        }
        return result;
    }
}

