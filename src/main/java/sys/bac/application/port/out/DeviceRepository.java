package sys.bac.application.port.out;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.domain.results.device.DevicesResult;

public interface DeviceRepository {

    DevicesResult getAllDevices(String query);

    DeviceResult create(Device device);

    DeviceResult getDeviceById(LongId id);

    NoContentResult delete(LongId id);

    NoContentResult update(LongId id, Device device);
}

