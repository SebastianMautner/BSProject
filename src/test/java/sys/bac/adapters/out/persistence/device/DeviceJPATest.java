package sys.bac.adapters.out.persistence.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.domain.results.device.JpaDevicesResult;
import sys.bac.application.port.out.CustomerRepository;
import sys.bac.application.port.out.DeviceRepository;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeviceJPATest {
    @Inject
    DeviceRepository dR;

    @Inject CustomerRepository cR;

    @Test
    @TestTransaction
    public void CreateErrorTest() {
        DeviceResult result = dR.create(null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    @TestTransaction
    public void CreateTest() {
        long cId = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"))
        .getResult().getcustomerId();
        DeviceResult result = dR.create(new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        if(result.hasError()) System.out.println(result.getMessage());
        assertEquals(new DeviceResult(new Device(result.getResult().getId(), cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), result);
    }
    
    @Test
    @TestTransaction
    public void getAllErrorTest() {
        JpaDevicesResult result = dR.getAllDevices("", -1, -1);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @SuppressWarnings("unused")
    @Test
    @TestTransaction
    public void PersistAndGetAllBlankTest() {
        long cId = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"))
        .getResult().getcustomerId();
        DeviceResult res = dR.create(new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        DeviceResult res2 = dR.create(new Device(null, cId, "231", "TV", "LG", "Smart TV", "Cracked Screen"));
        DeviceResult res3 = dR.create(new Device(null, cId, "312", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        JpaDevicesResult jpaResult = dR.getAllDevices("", 0, 2);
        assertEquals(new JpaDevicesResult(Arrays.asList(new Device(res.getResult().getId(), cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"),
        new Device(res2.getResult().getId(), cId, "231", "TV", "LG", "Smart TV", "Cracked Screen"))), jpaResult);
    }
    
    @SuppressWarnings("unused")
    @Test
    @TestTransaction
    public void PersistAndGetAllQueryTest() {
        long cId = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"))
        .getResult().getcustomerId();
        DeviceResult res = dR.create(new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        DeviceResult res2 = dR.create(new Device(null, cId, "231", "TV", "LG", "Smart TV", "Cracked Screen"));
        DeviceResult res3 = dR.create(new Device(null, cId, "312", "Phone", "Apple", "iPhone 16 Pro Max", "Cracked Screen"));
        JpaDevicesResult jpaResult = dR.getAllDevices("Apple", 0, 2);
        assertEquals(new JpaDevicesResult(Arrays.asList(new Device(res.getResult().getId(), cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"),
        new Device(res3.getResult().getId(), cId, "312", "Phone", "Apple", "iPhone 16 Pro Max", "Cracked Screen"))), jpaResult);
    }
    
    @Test
    @TestTransaction
    public void PersistAndGetAllOffsetTest() {
        long cId = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"))
        .getResult().getcustomerId();
        dR.create(new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        dR.create(new Device(null, cId, "231", "TV", "LG", "Smart TV", "Cracked Screen"));
        DeviceResult res = dR.create(new Device(null, cId, "312", "Phone", "Apple", "iPhone 16 Pro Max", "Cracked Screen"));
        JpaDevicesResult jpaResult = dR.getAllDevices("", 2, 2);
        assertEquals(new JpaDevicesResult(Arrays.asList(new Device(res.getResult().getId(), cId, "312", "Phone", "Apple", "iPhone 16 Pro Max", "Cracked Screen"))), jpaResult);
    }
    
    @Test
    @TestTransaction
    public void getBIErrorTest() {
        DeviceResult result = dR.getDeviceById(null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    @TestTransaction
    public void getBIEmptyTest() {
        DeviceResult result = dR.getDeviceById(new LongId(-1));
        assertTrue(result.isEmpty());
    }
    
    @Test
    @TestTransaction
    public void PersistAndGetBITest() {
        long cId = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"))
        .getResult().getcustomerId();
        DeviceResult res = dR.create(new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        DeviceResult result = dR.getDeviceById(res.getResult().getLongId());
        assertEquals(new DeviceResult(new Device(res.getResult().getId(), cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), result);
    }
    
    @Test
    @TestTransaction
    public void deleteErrorTest() {
        NoContentResult result = dR.delete(null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    @TestTransaction
    public void persistAndDelete() {
        long cId = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"))
        .getResult().getcustomerId();
        DeviceResult res = dR.create(new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        NoContentResult result = dR.delete(res.getResult().getLongId());
        assertEquals(new NoContentResult(), result);
        DeviceResult empty = dR.getDeviceById(res.getResult().getLongId());
        assertTrue(empty.isEmpty());
    }
    
    @Test
    @TestTransaction
    public void updateError() {
        NoContentResult result = dR.update(null, null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    @TestTransaction
    public void persistAndUpdateTest() {
        long cId = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"))
        .getResult().getcustomerId();
        DeviceResult res = dR.create(new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        NoContentResult result = dR.update(res.getResult().getLongId(), new Device(res.getResult().getId(), cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        assertEquals(new NoContentResult(), result);
        DeviceResult result2 = dR.getDeviceById(res.getResult().getLongId());
        assertEquals(new DeviceResult(new Device(res.getResult().getId(), cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), result2);
    }
    
    @Test
    @TestTransaction
    public void countErrorTest() {
        LongResult result = dR.count(null);
        assertTrue(result.hasError());
    }
    
    @Test
    @TestTransaction
    public void persistAndCountBlankTest() {
        long cId = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"))
        .getResult().getcustomerId();
        dR.create(new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        dR.create(new Device(null, cId, "231", "TV", "LG", "Smart TV", "Cracked Screen"));
        dR.create(new Device(null, cId, "312", "Phone", "Apple", "iPhone 16 Pro Max", "Cracked Screen"));
        LongResult result = dR.count("");
        assertEquals(3L, result.getResult());
        assertEquals(new LongResult(3L), result);
    }
    
    @Test
    @TestTransaction
    public void persistAndCountQueryTest() {
        long cId = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"))
        .getResult().getcustomerId();
        dR.create(new Device(null, cId, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        dR.create(new Device(null, cId, "231", "TV", "LG", "Smart TV", "Cracked Screen"));
        dR.create(new Device(null, cId, "312", "Phone", "Apple", "iPhone 16 Pro Max", "Cracked Screen"));
        LongResult result = dR.count("Apple");
        assertEquals(2L, result.getResult());
        assertEquals(new LongResult(2L), result);
    }
}