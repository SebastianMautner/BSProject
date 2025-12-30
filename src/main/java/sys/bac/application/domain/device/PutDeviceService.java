package sys.bac.application.domain.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.adapters.in.api.adapter.device.Mapper;
import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.port.in.device.PutDeviceUseCase;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class PutDeviceService implements PutDeviceUseCase {

    private final Mapper mapper = new Mapper();

    @Inject
    private DeviceRepository deviceRepo;

    @Override
    public NoContentResult updateDevice(LongId id, DeviceDTO device) {
        return deviceRepo.update(id, mapper.toDevice(device));
    }
}

