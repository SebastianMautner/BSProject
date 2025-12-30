package sys.bac.application.domain.results.device;

import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.MultiResult;

public class DevicesResult extends MultiResult<Device>{

    public DevicesResult(Page<Device> devices) {
        super(devices);
    }

    public DevicesResult() {}
}
