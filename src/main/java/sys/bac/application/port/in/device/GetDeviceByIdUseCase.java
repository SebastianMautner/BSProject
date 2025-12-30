package sys.bac.application.port.in.device;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.device.DeviceResult;

public interface GetDeviceByIdUseCase {
    DeviceResult loadDeviceById(LongId id);
}

