package sys.bac.application.port.in.device;

import sys.bac.application.domain.results.device.DevicesResult;

public interface GetDevicesUseCase {
    public DevicesResult findDevices(String query);
}
