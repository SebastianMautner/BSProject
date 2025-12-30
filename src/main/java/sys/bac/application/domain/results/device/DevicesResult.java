package sys.bac.application.domain.results.device;

import java.util.List;

import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.MultiResult;

public class DevicesResult extends MultiResult<Device> {

    public DevicesResult() {}

    public DevicesResult(List<Device> result) {
        super(result);
    }
}

