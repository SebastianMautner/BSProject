package sys.bac.application.domain.results.device;

import java.util.Objects;

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

    @Override
    public boolean equals(Object dto) {
        if (this == dto) return true;
        if(!(dto instanceof DeviceResult)) return false;
        DeviceResult dto2 = (DeviceResult) dto;
        return Objects.equals(this.result, dto2.result) &&
        this.error == dto2.error &&
        this.errorCode == dto2.errorCode &&
        this.errorMessage == dto2.errorMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, error, errorCode, errorMessage);
    }
}

