package sys.bac.adapters.in.api.adapter.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServerErrorException;
import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.adapters.in.api.models.OrdersApiResult;
import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.models.order.OrderStatus;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.domain.results.order.OrdersResult;
import sys.bac.application.port.in.order.DeleteOrderUseCase;
import sys.bac.application.port.in.order.GetOrderByIdUseCase;
import sys.bac.application.port.in.order.GetOrdersUseCase;
import sys.bac.application.port.in.order.PostOrderUseCase;
import sys.bac.application.port.in.order.PutOrderUseCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;


@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderAdapterTest {
    @InjectMock
    PostOrderUseCase poOUC;
    @InjectMock
    PutOrderUseCase puOUC;
    @InjectMock
    GetOrderByIdUseCase gOBIUC;
    @InjectMock
    GetOrdersUseCase gOUC;
    @InjectMock
    DeleteOrderUseCase dOUC;
    @Inject
    OrderServiceAdapter oSA;
    
    @Test
    public void getByIDEmptyTest() {
        when(gOBIUC.loadOrderById(any()))
        .thenReturn(new OrderResult());
        assertThrows(NotFoundException.class, () -> oSA.getOrderById(0));
    }
    
    @Test
    public void getByIdErrorTest() {
        when(gOBIUC.loadOrderById(any()))
        .thenReturn(new OrderResult(true));
        assertThrows(ServerErrorException.class, () -> oSA.getOrderById(1));
    }
    
    @Test
    public void getByIdTest() {
        when(gOBIUC.loadOrderById(any()))
        .thenReturn(new OrderResult(new Order(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED)));
        assertEquals(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED), oSA.getOrderById(0));
    }
    
    @Test
    public void getOrdersTest() {
        when(gOUC.findOrders(any(), anyInt(), anyInt()))
        .thenReturn(new OrdersResult(new Page<>(new ArrayList<>(), 0, 2, 2)));
        assertEquals(new OrdersApiResult(new ArrayList<>(), false, false), oSA.getOrders("", 0, 2));
    }

    @Test
    public void getOrdersErrorTest() {
        when(gOUC.findOrders(any(), anyInt(), anyInt()))
        .thenReturn(new OrdersResult(new Page<>(new ArrayList<>(), 0, 2, 0), true));
        assertThrows(ServerErrorException.class, () -> oSA.getOrders("", 0, 0));
    }

    @Test
    public void getOrdersNextTest() {
        when(gOUC.findOrders(any(), anyInt(), anyInt()))
        .thenReturn(new OrdersResult(new Page<>(new ArrayList<>(), 0, 2, 3)));
        assertEquals(new OrdersApiResult(new ArrayList<>(), true, false), oSA.getOrders("", 0, 0));
    }

    @Test
    public void getOrdersPrevTest() {
        when(gOUC.findOrders(any(), anyInt(), anyInt()))
        .thenReturn(new OrdersResult(new Page<>(new ArrayList<>(), 4, 2, 5)));
        assertEquals(new OrdersApiResult(new ArrayList<>(), false, true), oSA.getOrders("", 4, 2));
    }

    @Test
    public void getOrdersNextPrevTest() {
        when(gOUC.findOrders(any(), anyInt(), anyInt()))
        .thenReturn(new OrdersResult(new Page<>(new ArrayList<>(), 2, 2, 5)));
        assertEquals(new OrdersApiResult(new ArrayList<>(), true, true), oSA.getOrders("", 2, 2));
    }

    @Test
    public void createTest() {
        when(poOUC.createOrder(any()))
        .thenReturn(new OrderResult(new Order(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED)));
        assertEquals(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-04"), 100, 100000000, OrderStatus.RECEIVED), oSA.createOrder(new OrderDTO()));
    }

    @Test
    public void createErrorTest() {
        when(poOUC.createOrder(any()))
        .thenReturn(new OrderResult(true));
        assertThrows(ServerErrorException.class, () -> oSA.createOrder(new OrderDTO()));
    }

    @Test
    public void createEmptyTest() {
        assertThrows(BadRequestException.class, () -> oSA.createOrder(null));
    }

    @Test
    public void updateEmptyTest() {
        assertThrows(BadRequestException.class, () -> oSA.updateOrder(1, null));
    }

    @Test
    public void updateNotFoundTest() {
        when(puOUC.updateOrder(any(), any()))
        .thenReturn(new NoContentResult(404));
        assertThrows(NotFoundException.class, () -> oSA.updateOrder(1, new OrderDTO()));
    }

    @Test
    public void updateErrorTest() {
        when(puOUC.updateOrder(any(), any()))
        .thenReturn(new NoContentResult(true));
        assertThrows(ServerErrorException.class, () -> oSA.updateOrder(1, new OrderDTO()));
    }

    @Test
    public void updateTest() {
        when(puOUC.updateOrder(any(), any()))
        .thenReturn(new NoContentResult());
        try {
            oSA.updateOrder(1, new OrderDTO());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void deleteNotFoundTest() {
        when(dOUC.deleteOrder(any()))
        .thenReturn(new NoContentResult(404));
        assertThrows(NotFoundException.class, () -> oSA.deleteOrder(1));
    }

    @Test
    public void deleteErrorTest() {
        when(dOUC.deleteOrder(any()))
        .thenReturn(new NoContentResult(true));
        assertThrows(ServerErrorException.class, () -> oSA.deleteOrder(1));
    }

    @Test
    public void deleteTest() {
        when(dOUC.deleteOrder(any()))
        .thenReturn(new NoContentResult());
        try {
            oSA.deleteOrder(1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
