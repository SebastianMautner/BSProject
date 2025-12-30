package sys.bac.application.domain.results.device;

import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.SingleResult;

public class DeviceResult extends SingleResult<Device> {

    public DeviceResult() {}

    public DeviceResult(Device result) {
        super(result);
    }
}

