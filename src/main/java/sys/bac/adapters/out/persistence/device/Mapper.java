package sys.bac.adapters.out.persistence.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import sys.bac.adapters.out.persistence.customer.CustomerJPAEntity;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.device.DeviceResult;

@ApplicationScoped
public class Mapper {

    @Inject
    EntityManager eM; 

    public DeviceJPAEntity toJPA(Device device) {
        DeviceJPAEntity e = new DeviceJPAEntity();
        e.setSerialNumber(device.getSerialNumber());
        e.setType(device.getType());
        e.setBrand(device.getBrand());
        e.setModel(device.getModel());
        e.setNotes(device.getNotes());

        CustomerJPAEntity customer = eM.find(CustomerJPAEntity.class, device.getCustomerId());
        if(customer == null) throw new IllegalArgumentException("No Customer with Id" + device.getCustomerId());
        e.setCustomer(customer);
        return e;
    }

    public Device toDevice(DeviceJPAEntity e) {
        return new Device(
                new LongId(e.getId()),
                e.getCustomer().getId(),
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

