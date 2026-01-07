package sys.bac.adapters.out.persistence.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.models.order.OrderStatus;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.domain.results.order.JpaOrdersResult;
import sys.bac.application.port.out.CustomerRepository;
import sys.bac.application.port.out.DeviceRepository;
import sys.bac.application.port.out.OrderRepository;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderJPATest {

    @Inject
    OrderRepository oR;

    @Inject DeviceRepository dR;

    @Inject CustomerRepository cR; 

    @Test
    public void CreateErrorTest() {
        OrderResult result = oR.create(null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    @TestTransaction
    public void CreateTest() {
        CustomerResult c = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"));
        assertFalse(c.hasError(), c.getMessage());
        DeviceResult d = dR.create(new Device(null, c.getResult().getcustomerId(), "123", "Phone", "Apple", "iPhone 17 Pro Max", "CrackedScreen"));
        assertFalse(d.hasError(), d.getMessage() + d.getResult().getId());
        OrderResult result = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        assertFalse(result.hasError(), result.getMessage());
        assertEquals(new OrderResult(new Order(result.getResult().getId(), c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED)), result);
    }
    
    @Test
    public void getAllErrorTest() {
        JpaOrdersResult result = oR.getAllOrders("", -1, -1);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @SuppressWarnings("unused")
    @Test
    @TestTransaction
    public void PersistAndGetAllBlankTest() {
        CustomerResult c = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"));
        assertFalse(c.hasError(), c.getMessage());
        DeviceResult d = dR.create(new Device(null, c.getResult().getcustomerId(), "123", "Phone", "Apple", "iPhone 17 Pro Max", "CrackedScreen"));
        assertFalse(d.hasError(), d.getMessage());
        OrderResult res = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        OrderResult res2 = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "No Battery", LocalDate.parse("2020-11-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        OrderResult res3 = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "Snapped in 2", LocalDate.parse("2020-10-31"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        JpaOrdersResult result = oR.getAllOrders("", 0, 2);
        assertFalse(result.hasError(), result.getMessage());
        assertEquals(new JpaOrdersResult(Arrays.asList(new Order(res.getResult().getId(), c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED),
        new Order(res2.getResult().getId(), c.getResult().getcustomerId(), d.getResult().getId(), "No Battery", LocalDate.parse("2020-11-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED))), result);
    }
    
    @SuppressWarnings("unused")
    @Test
    @TestTransaction
    public void PersistAndGetAllQueryTest() {
        CustomerResult c = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"));
        assertFalse(c.hasError(), c.getMessage());
        DeviceResult d = dR.create(new Device(null, c.getResult().getcustomerId(), "123", "Phone", "Apple", "iPhone 17 Pro Max", "CrackedScreen"));
        assertFalse(d.hasError(), d.getMessage());
        OrderResult res = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        OrderResult res2 = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "NoBattery", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        OrderResult res3 = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-11-29"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        JpaOrdersResult result = oR.getAllOrders("CrackedScreen", 0, 2);
        assertFalse(result.hasError(), result.getMessage());
        assertEquals(new JpaOrdersResult(Arrays.asList(new Order(res.getResult().getId(), c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED),
        new Order(res3.getResult().getId(), c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-11-29"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED))), result);
    }
    
    @Test
    @TestTransaction
    public void PersistAndGetAllOffsetTest() {
        CustomerResult c = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"));
        assertFalse(c.hasError(), c.getMessage());
        DeviceResult d = dR.create(new Device(null, c.getResult().getcustomerId(), "123", "Phone", "Apple", "iPhone 17 Pro Max", "CrackedScreen"));
        assertFalse(d.hasError(), d.getMessage());
        oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        OrderResult res3 = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "Snapped in 2", LocalDate.parse("2020-10-29"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        JpaOrdersResult result = oR.getAllOrders("", 2, 2);
        assertFalse(result.hasError(), result.getMessage());
        assertEquals(new JpaOrdersResult(Arrays.asList(new Order(res3.getResult().getId(), c.getResult().getcustomerId(), d.getResult().getId(), "Snapped in 2", LocalDate.parse("2020-10-29"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED))), result);
    }
    
    @Test
    public void getBIErrorTest() {
        OrderResult result = oR.getOrderById(null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    public void getBIEmptyTest() {
        OrderResult result = oR.getOrderById(new LongId(-1));
        assertTrue(result.isEmpty());
    }
    
    @Test
    @TestTransaction
    public void PersistAndGetBITest() {
        CustomerResult c = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"));
        assertFalse(c.hasError(), c.getMessage());
        DeviceResult d = dR.create(new Device(null, c.getResult().getcustomerId(), "123", "Phone", "Apple", "iPhone 17 Pro Max", "CrackedScreen"));
        assertFalse(d.hasError(), d.getMessage());
        OrderResult res = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        OrderResult result = oR.getOrderById(res.getResult().getLongId());
        assertFalse(result.hasError(), result.getMessage());
        assertEquals(new OrderResult(new Order(res.getResult().getId(), c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED)), result);
    }
    
    @Test
    public void deleteErrorTest() {
        NoContentResult result = oR.delete(null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    @TestTransaction
    public void persistAndDelete() {
        CustomerResult c = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"));
        assertFalse(c.hasError(), c.getMessage());
        DeviceResult d = dR.create(new Device(null, c.getResult().getcustomerId(), "123", "Phone", "Apple", "iPhone 17 Pro Max", "CrackedScreen"));
        assertFalse(d.hasError(), d.getMessage());
        OrderResult res = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        NoContentResult result = oR.delete(res.getResult().getLongId());
        assertFalse(result.hasError(), result.getMessage());
        assertEquals(new NoContentResult(), result);
        OrderResult empty = oR.getOrderById(res.getResult().getLongId());
        assertTrue(empty.isEmpty());
    }
    
    @Test
    public void updateError() {
        NoContentResult result = oR.update(null, null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    @TestTransaction
    public void persistAndUpdateTest() {
        CustomerResult c = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"));
        assertFalse(c.hasError(), c.getMessage());
        DeviceResult d = dR.create(new Device(null, c.getResult().getcustomerId(), "123", "Phone", "Apple", "iPhone 17 Pro Max", "CrackedScreen"));
        assertFalse(d.hasError(), d.getMessage());
        OrderResult res = oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        NoContentResult result = oR.update(res.getResult().getLongId(), new Order(res.getResult().getId(), c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        assertFalse(result.hasError(), result.getMessage());
        assertEquals(new NoContentResult(), result);
        OrderResult result2 = oR.getOrderById(res.getResult().getLongId());
        assertEquals(new OrderResult(new Order(res.getResult().getId(), c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED)), result2);
    }
    
    @Test
    public void countErrorTest() {
        LongResult result = oR.count(null);
        assertTrue(result.hasError());
    }
    
    @Test
    @TestTransaction
    public void persistAndCountBlankTest() {
        CustomerResult c = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"));
        assertFalse(c.hasError(), c.getMessage());
        DeviceResult d = dR.create(new Device(null, c.getResult().getcustomerId(), "123", "Phone", "Apple", "iPhone 17 Pro Max", "CrackedScreen"));
        assertFalse(d.hasError(), d.getMessage());
        oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        LongResult result = oR.count("");
        assertFalse(result.hasError(), result.getMessage());
        assertEquals(3L, result.getResult());
        assertEquals(new LongResult(3L), result);
    }
    
    @Test
    @TestTransaction
    public void persistAndCountQueryTest() {
        CustomerResult c = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 123 12345678"));
        assertFalse(c.hasError(), c.getMessage());
        DeviceResult d = dR.create(new Device(null, c.getResult().getcustomerId(), "123", "Phone", "Apple", "iPhone 17 Pro Max", "CrackedScreen"));
        assertFalse(d.hasError(), d.getMessage());
        oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "No Battery", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        oR.create(new Order(null, c.getResult().getcustomerId(), d.getResult().getId(), "CrackedScreen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED));
        LongResult result = oR.count("CrackedScreen");
        assertFalse(result.hasError(), result.getMessage());
        assertEquals(2L, result.getResult());
        assertEquals(new LongResult(2L), result);
    }
}