package sys.bac.application.domain.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.Mock;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.domain.results.customer.CustomersResult;
import sys.bac.application.domain.results.customer.JpaCustomersResult;
import sys.bac.application.port.out.CustomerRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository cR;

    @InjectMocks
    DeleteCustomerService dCS;

    @InjectMocks
    GetCustomersService gCS;

    @InjectMocks
    GetCustomerService gCBIS;

    @InjectMocks
    PostCustomerService poCS;

    @InjectMocks
    PutCustomerService puCS;

    private final LongId ID = new LongId(1L);

    @BeforeEach
    void setUp() {
    }

    @Test
    public void deleteEmpty404Test() {
        when(cR.getCustomerById(any()))
        .thenReturn(new CustomerResult());

        NoContentResult result = dCS.deleteCustomer(ID);
        assertTrue(result.hasError());
        assertEquals(404, result.getErrorCode());
        assertEquals("NotFound", result.getMessage());
    }

    @Test
    public void deleteError500Test() {
        when(cR.getCustomerById(any()))
        .thenReturn(new CustomerResult(true));

        NoContentResult resutl = dCS.deleteCustomer(ID);
        assertTrue(resutl.hasError());
        assertEquals(500, resutl.getErrorCode());
    }

    @Test
    public void deleteTest() {
        when(cR.getCustomerById(any()))
        .thenReturn(new CustomerResult(new Customer()));
        when(cR.delete(any()))
        .thenReturn(new NoContentResult());

        NoContentResult result = dCS.deleteCustomer(ID);
        assertFalse(result.hasError());
        assertEquals(new NoContentResult(), result);
    }

    @Test
    public void getByIdEmptyTest() {
        when(cR.getCustomerById(any()))
        .thenReturn(new CustomerResult());

        CustomerResult result = gCBIS.loadCustomerById(ID);
        assertTrue(result.hasError());
        assertEquals(404, result.getErrorCode());
        assertEquals("NotFound", result.getMessage());
    }

    @Test
    public void getByIdTest() {
        when(cR.getCustomerById(any()))
        .thenReturn(new CustomerResult(new Customer(1, "Bond", "James", "test@test.de", "+44 12312345678")));

        CustomerResult result = gCBIS.loadCustomerById(ID);
        assertFalse(result.hasError());
        assertEquals(new CustomerResult(new Customer(1, "Bond", "James", "test@test.de", "+44 12312345678")), result);
    }

    @Test
    public void getAllErrorErrorTest() {
        when(cR.getAllCustomers(any(), anyInt(), anyInt()))
        .thenReturn(new JpaCustomersResult(true));
        when(cR.count(any()))
        .thenReturn(new LongResult(true));

        CustomersResult result = gCS.findCustomers("", 0, 0);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }

    @Test
    public void getAllTest() {
        when(cR.getAllCustomers(any(), anyInt(), anyInt()))
        .thenReturn(new JpaCustomersResult(new ArrayList<>()));
        when(cR.count(any()))
        .thenReturn(new LongResult(-1L));

        CustomersResult result = gCS.findCustomers("", 0, 0);
        assertFalse(result.hasError());
        assertEquals(new CustomersResult(new Page<>(new ArrayList<>(), 0, 0, -1)), result);
    }

    @Test
    public void postTest() {
        when(cR.create(any()))
        .thenReturn(new CustomerResult(new Customer(1, "Bond", "James", "test@test.de", "+44 12312345678")));

        CustomerResult result = poCS.createCustomer(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 12312345678"));
        assertFalse(result.hasError());
        assertEquals(new CustomerResult(new Customer(1, "Bond", "James", "test@test.de", "+44 12312345678")), result);
    }

    @Test
    public void putEmpty404Test() {
        when(cR.getCustomerById(any()))
        .thenReturn(new CustomerResult());

        NoContentResult result = puCS.updateCustomer(ID, new CustomerDTO());
        assertTrue(result.hasError());
        assertEquals(404, result.getErrorCode());
        assertEquals("NotFound", result.getMessage());
    }

    @Test
    public void putError500Test() {
        when(cR.getCustomerById(any()))
        .thenReturn(new CustomerResult(true));

        NoContentResult result = puCS.updateCustomer(ID, new CustomerDTO());
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }

    @Test
    public void putTest() {
        when(cR.update(any(), any()))
        .thenReturn(new NoContentResult());
        when(cR.getCustomerById(any()))
        .thenReturn(new CustomerResult(new Customer()));
        NoContentResult result = puCS.updateCustomer(ID, new CustomerDTO());
        assertFalse(result.hasError());
        assertEquals(new NoContentResult(), result);
    }
}
