package sys.bac.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import sys.bac.adapters.out.persistence.customer.CustomerJPAEntity;
import sys.bac.adapters.out.persistence.customer.Mapper;
import sys.bac.adapters.out.persistence.device.DeviceJPAEntity;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.models.device.Device;

@ApplicationScoped
public class TestUtils {
    
    @Inject
    EntityManager eM;

    @Inject
    sys.bac.adapters.out.persistence.device.Mapper devMapper;

    Mapper customerMapper = new Mapper();
    private int cId = -1;

    @Transactional
    public void clearCustomers() {
        eM.createQuery("DELETE FROM CustomerJPAEntity").executeUpdate();
        cId = -1;
    }

    @Transactional
    public void clearDevices() {
        eM.createQuery("DELETE FROM DeviceJPAEntity").executeUpdate();
        clearCustomers();
        cId = -1;
    }

    @Transactional
    public void clearOrders() {
        eM.createQuery("DELETE FROM OrderJPAEntity").executeUpdate();
        clearDevices();
        cId = -1;
    }

    @Transactional
    public int createCustomerFixture() {
        Customer c = new Customer(null, "Bond", "James", "test@test.co.uk", "+44 12312345678");

        CustomerJPAEntity entity = customerMapper.toJPA(c);
        eM.persist(entity);
        eM.flush();
        int id = (int) entity.getId();
        return cId = id;
    }

    @Transactional
    public int createDeviceFixture() {
        if (cId == -1) {
            cId = createCustomerFixture();
        }
        Device d = new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen");
        DeviceJPAEntity entity = devMapper.toJPA(d);
        eM.persist(entity);
        eM.flush();
        return (int) entity.getId();
    }
}
