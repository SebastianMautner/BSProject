package sys.bac.application.port.out;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.domain.results.device.JpaDevicesResult;

public interface DeviceRepository {

    JpaDevicesResult getAllDevices(String query, int offset, int size);

    DeviceResult create(Device device);

    DeviceResult getDeviceById(LongId id);

    NoContentResult delete(LongId id);

    NoContentResult update(LongId id, Device device);

    LongResult count(String query);
}

