package sys.bac.application.domain.results.device;

import java.util.Objects;

import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.MultiResult;

public class DevicesResult extends MultiResult<Device>{

    public DevicesResult(Page<Device> devices) {
        super(devices);
    }

    public DevicesResult() {}

    public DevicesResult(Page<Device> device, boolean error) {
        super();
        this.error = error;
    } 

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof DevicesResult)) return false;
        DevicesResult res = (DevicesResult) o;
        return Objects.equals(this.result, res.result) &&
        this.error == res.error &&
        this.errorCode == res.errorCode &&
        this.errorMessage == res.errorMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, error, errorCode, errorMessage);
    }
}
