package sys.bac.application.domain.results.device;

import java.util.ArrayList;
import java.util.List;

import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.JpaMultiResult;

public class JpaDevicesResult extends JpaMultiResult<Device> {

    public JpaDevicesResult() {}

    public JpaDevicesResult(List<Device> result) {
        super(result);
    }

    public JpaDevicesResult(boolean error, int errorCode) {
        super();
        this.results = new ArrayList<>();
        this.error = error;
        this.errorCode = errorCode;
    }

    public JpaDevicesResult(boolean error) {
        super();
        this.results = new ArrayList<>();
        this.error = error;
    }
}

