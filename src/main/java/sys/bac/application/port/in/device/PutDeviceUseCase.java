package sys.bac.application.port.in.device;

import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;

public interface PutDeviceUseCase {
    NoContentResult updateDevice(LongId id, DeviceDTO device);
}

