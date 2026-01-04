package sys.bac.adapters.in.api.adapter.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServerErrorException;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.adapters.in.api.models.CustomersApiResult;
import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.domain.results.customer.CustomersResult;
import sys.bac.application.port.in.customer.DeleteCustomerUseCase;
import sys.bac.application.port.in.customer.GetCustomerByIdUseCase;
import sys.bac.application.port.in.customer.GetCustomersUseCase;
import sys.bac.application.port.in.customer.PostCustomerUseCase;
import sys.bac.application.port.in.customer.PutCustomerUseCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;


@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerAdapterTest {
    @InjectMock
    PostCustomerUseCase poCUC;
    @InjectMock
    PutCustomerUseCase puCUC;
    @InjectMock
    GetCustomerByIdUseCase gCBIUC;
    @InjectMock
    GetCustomersUseCase gCUC;
    @InjectMock
    DeleteCustomerUseCase dCUC;
    @Inject
    CustomerServiceAdapter cSA;
    
    @Test
    public void getByIDEmptyTest() {
        when(gCBIUC.loadCustomerById(any()))
        .thenReturn(new CustomerResult());
        assertThrows(NotFoundException.class, () -> cSA.getCustomerById(0));
    }
    
    @Test
    public void getByIdErrorTest() {
        when(gCBIUC.loadCustomerById(any()))
        .thenReturn(new CustomerResult(true));
        assertThrows(ServerErrorException.class, () -> cSA.getCustomerById(0));
    }
    
    @Test 
    public void getByIdTest() {
        when(gCBIUC.loadCustomerById(any()))
        .thenReturn(new CustomerResult(new Customer(1, "Bond", "James", "test@test.de", "+44 123 12345678")));
        assertEquals(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 123 12345678"), cSA.getCustomerById(0));
    }
    
    @Test
    public void getCustomersTest() {
        when(gCUC.findCustomers(any(), anyInt(), anyInt()))
        .thenReturn(new CustomersResult(new Page<>(new ArrayList<>(), 0, 2, 2)));
        assertEquals(new CustomersApiResult(new ArrayList<>(), false, false), cSA.getCustomers("", 0, 2));
    }

    @Test
    public void getCustomersErrorTest() {
        when(gCUC.findCustomers(any(), anyInt(), anyInt()))
        .thenReturn(new CustomersResult(new Page<>(new ArrayList<>(), 0, 2, 0), true));
        assertThrows(ServerErrorException.class, () -> cSA.getCustomers("", 0, 0));
    }

    @Test
    public void getCustomersNextTest() {
        when(gCUC.findCustomers(any(), anyInt(), anyInt()))
        .thenReturn(new CustomersResult(new Page<>(new ArrayList<>(), 0, 2, 3)));
        assertEquals(new CustomersApiResult(new ArrayList<>(), true, false), cSA.getCustomers("", 0, 0));
    }

    @Test
    public void getCustomersPrevTest() {
        when(gCUC.findCustomers(any(), anyInt(), anyInt()))
        .thenReturn(new CustomersResult(new Page<>(new ArrayList<>(), 4, 2, 5)));
        assertEquals(new CustomersApiResult(new ArrayList<>(), false, true), cSA.getCustomers("", 4, 2));
    }

    @Test
    public void getCustomersNextPrevTest() {
        when(gCUC.findCustomers(any(), anyInt(), anyInt()))
        .thenReturn(new CustomersResult(new Page<>(new ArrayList<>(), 2, 2, 5)));
        assertEquals(new CustomersApiResult(new ArrayList<>(), true, true), cSA.getCustomers("", 2, 2));
    }

    @Test
    public void createTest() {
        when(poCUC.createCustomer(any()))
        .thenReturn(new CustomerResult(new Customer(1, "Bond", "James", "test@test.de", "+44 123 12345678")));
        assertEquals(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 123 12345678"), cSA.createCustomer(new CustomerDTO()));
    }

    @Test
    public void createErrorTest() {
        when(poCUC.createCustomer(any()))
        .thenReturn(new CustomerResult(true));
        assertThrows(ServerErrorException.class, () -> cSA.createCustomer(new CustomerDTO()));
    }

    @Test
    public void createEmptyTest() {
        assertThrows(BadRequestException.class, () -> cSA.createCustomer(null));
    }

    @Test
    public void updateEmptyTest() {
        assertThrows(BadRequestException.class, () -> cSA.updateCustomer(1, null));
    }

    @Test
    public void updateNotFoundTest() {
        when(puCUC.updateCustomer(any(), any()))
        .thenReturn(new NoContentResult(404));
        assertThrows(NotFoundException.class, () -> cSA.updateCustomer(1, new CustomerDTO()));
    }

    @Test
    public void updateErrorTest() {
        when(puCUC.updateCustomer(any(), any()))
        .thenReturn(new NoContentResult(true));
        assertThrows(ServerErrorException.class, () -> cSA.updateCustomer(1, new CustomerDTO()));
    }

    @Test
    public void updateTest() {
        when(puCUC.updateCustomer(any(), any()))
        .thenReturn(new NoContentResult());
        try {
            cSA.updateCustomer(1, new CustomerDTO());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void deleteNotFoundTest() {
        when(dCUC.deleteCustomer(any()))
        .thenReturn(new NoContentResult(404));
        assertThrows(NotFoundException.class, () -> cSA.deleteCustomer(1));
    }

    @Test
    public void deleteErrorTest() {
        when(dCUC.deleteCustomer(any()))
        .thenReturn(new NoContentResult(true));
        assertThrows(ServerErrorException.class, () -> cSA.deleteCustomer(1));
    }

    @Test
    public void deleteTest() {
        when(dCUC.deleteCustomer(any()))
        .thenReturn(new NoContentResult());
        try {
            cSA.deleteCustomer(1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
