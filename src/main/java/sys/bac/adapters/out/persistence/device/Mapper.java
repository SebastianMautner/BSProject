package sys.bac.adapters.out.persistence.device;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.device.DeviceResult;

public class Mapper {

    public DeviceJPAEntity toJPA(Device device) {
        DeviceJPAEntity e = new DeviceJPAEntity();
        e.setCustomerId(device.getCustomerId());
        e.setSerialNumber(device.getSerialNumber());
        e.setType(device.getType());
        e.setBrand(device.getBrand());
        e.setModel(device.getModel());
        e.setNotes(device.getNotes());
        return e;
    }

    public Device toDevice(DeviceJPAEntity e) {
        return new Device(
                new LongId(e.getId()),
                e.getCustomerId(),
                e.getSerialNumber(),
                e.getType(),
                e.getBrand(),
                e.getModel(),
                e.getNotes()
        );
    }

    public DeviceResult toDeviceResult(Device device) {
        return new DeviceResult(device);
    }

    public DeviceResult toDeviceResult(DeviceJPAEntity entity) {
        return toDeviceResult(toDevice(entity));
    }
}

