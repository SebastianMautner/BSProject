package sys.bac.application.port.in.device;

import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.application.domain.results.device.DeviceResult;

public interface PostDeviceUseCase {
    DeviceResult createDevice(DeviceDTO device);
}

