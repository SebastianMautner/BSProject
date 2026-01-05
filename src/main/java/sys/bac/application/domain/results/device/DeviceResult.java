package sys.bac.application.domain.results.device;

import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.SingleResult;

public class DeviceResult extends SingleResult<Device> {

    public DeviceResult() {}

    public DeviceResult(Device result) {
        super(result);
    }

    public DeviceResult(boolean error) {
        this.result = new Device();
        this.error = error;
    }

    public DeviceResult(boolean error, int errorCode) {
        this.result = new Device();
        this.error = error;
        this.errorCode = errorCode;
    }
}

