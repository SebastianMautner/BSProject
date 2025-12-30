package sys.bac.application.port.in.device;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;

public interface DeleteDeviceUseCase {
    NoContentResult deleteDevice(LongId id);
}

