package sys.bac.application.domain.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.application.domain.results.device.DevicesResult;
import sys.bac.application.port.in.device.GetDevicesUseCase;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class GetDevicesService implements GetDevicesUseCase {

    @Inject
    private DeviceRepository deviceRepo;

    @Override
    public DevicesResult findDevices(String query) {
        return deviceRepo.getAllDevices(query);
    }
}
