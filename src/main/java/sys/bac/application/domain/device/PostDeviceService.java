package sys.bac.application.domain.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.adapters.in.api.adapter.device.Mapper;
import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.port.in.device.PostDeviceUseCase;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class PostDeviceService implements PostDeviceUseCase {

    private final Mapper mapper = new Mapper();

    @Inject
    private DeviceRepository deviceRepo;

    @Override
    public DeviceResult createDevice(DeviceDTO device) {
        return deviceRepo.create(mapper.toDevice(device));
    }
}

