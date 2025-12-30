package sys.bac.application.domain.results.device;

import java.util.List;

import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.JpaMultiResult;

public class JpaDevicesResult extends JpaMultiResult<Device> {

    public JpaDevicesResult() {}

    public JpaDevicesResult(List<Device> result) {
        super(result);
    }
}

