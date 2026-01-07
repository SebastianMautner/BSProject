package sys.bac.application.domain.results.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof JpaDevicesResult)) return false;
        JpaDevicesResult res = (JpaDevicesResult) o;
        return Objects.equals(this.results, res.results) &&
        this.error == res.error &&
        this.errorCode == res.errorCode &&
        this.errorMessage == res.errorMessage;
    }

    @Override 
    public int hashCode() {
        return Objects.hash(results, error, errorCode, errorMessage);
    }
}

