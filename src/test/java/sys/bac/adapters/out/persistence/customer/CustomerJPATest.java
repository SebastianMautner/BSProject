package sys.bac.adapters.out.persistence.customer;

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
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.domain.results.customer.JpaCustomersResult;
import sys.bac.application.port.out.CustomerRepository;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerJPATest {
    @Inject
    CustomerRepository cR;
    
    @Test
    @TestTransaction
    public void CreateErrorTest() {
        CustomerResult result = cR.create(null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    @TestTransaction
    public void CreateTest() {
        CustomerResult result = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 12312345678"));
        assertEquals(new CustomerResult(new Customer(result.getResult().getcustomerId(), "Bond", "James", "test@test.de", "+44 12312345678")), result);
    }
    
    @Test
    public void getAllErrorTest() {
        JpaCustomersResult result = cR.getAllCustomers("", -1, -1);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @SuppressWarnings("unused")
    @Test
    @TestTransaction
    public void PersistAndGetAllBlankTest() {
        CustomerResult res = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 12312345678"));
        CustomerResult res2 = cR.create(new Customer(null, "Moneypenny", "Eve", "test@test.de", "+44 12312345678"));
        CustomerResult res3 = cR.create(new Customer(null, "Blofield", "James", "test@test.de", "+44 12312345678"));
        JpaCustomersResult jpaResult = cR.getAllCustomers("", 0, 2);
        assertEquals(new JpaCustomersResult(Arrays.asList(new Customer(res.getResult().getcustomerId(), "Bond", "James", "test@test.de", "+44 12312345678"),
        new Customer(res2.getResult().getcustomerId(), "Moneypenny", "Eve", "test@test.de", "+44 12312345678"))), jpaResult);
    }
    
    @SuppressWarnings("unused")
    @Test
    @TestTransaction
    public void PersistAndGetAllQueryTest() {
        CustomerResult res = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 12312345678"));
        CustomerResult res2 = cR.create(new Customer(null, "Moneypenny", "Eve", "test@test.de", "+44 12312345678"));
        CustomerResult res3 = cR.create(new Customer(null, "Blofield", "James", "test@test.de", "+44 12312345678"));
        JpaCustomersResult jpaResult = cR.getAllCustomers("James", 0, 2);
        assertEquals(new JpaCustomersResult(Arrays.asList(new Customer(res.getResult().getcustomerId(), "Bond", "James", "test@test.de", "+44 12312345678"),
        new Customer(res3.getResult().getcustomerId(), "Blofield", "James", "test@test.de", "+44 12312345678"))), jpaResult);
    }
    
    @Test
    @TestTransaction
    public void PersistAndGetAllOffsetTest() {
        cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 12312345678"));
        cR.create(new Customer(null, "Moneypenny", "Eve", "test@test.de", "+44 12312345678"));
        cR.create(new Customer(null, "Blofield", "James", "test@test.de", "+44 12312345678"));
        JpaCustomersResult jpaResult = cR.getAllCustomers("", 2, 2);
        assertEquals(new JpaCustomersResult(Arrays.asList(new Customer(3, "Blofield", "James", "test@test.de", "+44 12312345678"))), jpaResult);
    }
    
    @Test
    @TestTransaction
    public void getBIErrorTest() {
        CustomerResult result = cR.getCustomerById(null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }
    
    @Test
    @TestTransaction
    public void getBIEmptyTest() {
        CustomerResult result = cR.getCustomerById(new LongId(-1));
        assertTrue(result.isEmpty());
    }
    
    @Test
    @TestTransaction
    public void PersistAndGetBITest() {
        CustomerResult res = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 12312345678"));
        CustomerResult result = cR.getCustomerById(res.getResult().getLongId());
        assertEquals(new CustomerResult(new Customer(res.getResult().getcustomerId(), "Bond", "James", "test@test.de", "+44 12312345678")), result);
    }
    
    @Test
    public void deleteErrorTest() {
        NoContentResult result = cR.delete(null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }

    @Test
    @TestTransaction
    public void persistAndDelete() {
        CustomerResult res = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 12312345678"));
        NoContentResult result = cR.delete(res.getResult().getLongId());
        assertEquals(new NoContentResult(), result);
        CustomerResult empty = cR.getCustomerById(res.getResult().getLongId());
        assertTrue(empty.isEmpty());
    }

    @Test
    @TestTransaction
    public void updateError() {
        NoContentResult result = cR.update(null, null);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }

    @Test
    @TestTransaction
    public void persistAndUpdateTest() {
        CustomerResult res = cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 12312345678"));
        NoContentResult result = cR.update(res.getResult().getLongId(), new Customer(res.getResult().getcustomerId(), "Moneypenny", "Eve", "test@test.de", "+44 12312345678"));
        assertEquals(new NoContentResult(), result);
        CustomerResult result2 = cR.getCustomerById(res.getResult().getLongId());
        assertEquals(new CustomerResult(new Customer(res.getResult().getcustomerId(), "Moneypenny", "Eve", "test@test.de", "+44 12312345678")), result2);
    }

    @Test
    @TestTransaction
    public void countErrorTest() {
        LongResult result = cR.count(null);
        assertTrue(result.hasError());
    }

    @Test
    @TestTransaction
    public void persistAndCountBlankTest() {
        cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 12312345678"));
        cR.create(new Customer(null, "Moneypenny", "Eve", "test@test.de", "+44 12312345678"));
        cR.create(new Customer(null, "Blofield", "James", "test@test.de", "+44 12312345678"));
        LongResult result = cR.count("");
        assertEquals(3L, result.getResult());
        assertEquals(new LongResult(3L), result);
    }

    @Test
    @TestTransaction
    public void persistAndCountQueryTest() {
        cR.create(new Customer(null, "Bond", "James", "test@test.de", "+44 12312345678"));
        cR.create(new Customer(null, "Moneypenny", "Eve", "test@test.de", "+44 12312345678"));
        cR.create(new Customer(null, "Blofield", "James", "test@test.de", "+44 12312345678"));
        LongResult result = cR.count("James");
        assertEquals(2L, result.getResult());
        assertEquals(new LongResult(2L), result);
    }
}