package sys.bac.application.domain.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.adapters.in.api.adapter.device.Mapper;
import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.port.in.device.PutDeviceUseCase;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class PutDeviceService implements PutDeviceUseCase {

    private final Mapper mapper = new Mapper();

    @Inject
    private DeviceRepository deviceRepo;

    @Override
    public NoContentResult updateDevice(LongId id, DeviceDTO device) {
        DeviceResult exists = deviceRepo.getDeviceById(id);
        NoContentResult result = new NoContentResult();
        
        if (exists.isEmpty()) {
            result.setError(404, "NotFound");
        } else if (exists.hasError()) {
            result.setError(500, exists.getMessage());
        } else {
            result = deviceRepo.update(id, mapper.toDevice(device));
        }
        return result;
    }
}

