package sys.bac.application.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.Mock;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.models.order.OrderStatus;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.domain.results.order.OrdersResult;
import sys.bac.application.domain.results.order.JpaOrdersResult;
import sys.bac.application.port.out.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    OrderRepository oR;

    @InjectMocks
    DeleteOrderService dOS;

    @InjectMocks
    GetOrdersService gOS;

    @InjectMocks
    GetOrderService gOBIS;

    @InjectMocks
    PostOrderService poOS;

    @InjectMocks
    PutOrderService puOS;

    private final LongId ID = new LongId(1L);

    @BeforeEach
    void setUp() {
    }

    @Test
    public void deleteEmpty404Test() {
        when(oR.getOrderById(any()))
        .thenReturn(new OrderResult());

        NoContentResult result = dOS.deleteOrder(ID);
        assertTrue(result.hasError());
        assertEquals(404, result.getErrorCode());
        assertEquals("NotFound", result.getMessage());
    }

    @Test
    public void deleteError500Test() {
        when(oR.getOrderById(any()))
        .thenReturn(new OrderResult(true));

        NoContentResult resutl = dOS.deleteOrder(ID);
        assertTrue(resutl.hasError());
        assertEquals(500, resutl.getErrorCode());
    }

    @Test
    public void deleteTest() {
        when(oR.getOrderById(any()))
        .thenReturn(new OrderResult(new Order()));
        when(oR.delete(any()))
        .thenReturn(new NoContentResult());

        NoContentResult result = dOS.deleteOrder(ID);
        assertFalse(result.hasError());
        assertEquals(new NoContentResult(), result);
    }

    @Test
    public void getByIdEmptyTest() {
        when(oR.getOrderById(any()))
        .thenReturn(new OrderResult());

        OrderResult result = gOBIS.loadOrderById(ID);
        assertTrue(result.hasError());
        assertEquals(404, result.getErrorCode());
        assertEquals("NotFound", result.getMessage());
    }

    @Test
    public void getByIdTest() {
        when(oR.getOrderById(any()))
        .thenReturn(new OrderResult(new Order(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-05"), 100, 10000000, OrderStatus.RECEIVED)));

        OrderResult result = gOBIS.loadOrderById(ID);
        assertFalse(result.hasError());
        assertEquals(new OrderResult(new Order(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-05"), 100, 10000000, OrderStatus.RECEIVED)), result);
    }

    @Test
    public void getAllErrorErrorTest() {
        when(oR.getAllOrders(any(), anyInt(), anyInt()))
        .thenReturn(new JpaOrdersResult(true));
        when(oR.count(any()))
        .thenReturn(new LongResult(true));

        OrdersResult result = gOS.findOrders("", 0, 0);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }

    @Test
    public void getAllTest() {
        when(oR.getAllOrders(any(), anyInt(), anyInt()))
        .thenReturn(new JpaOrdersResult(new ArrayList<>()));
        when(oR.count(any()))
        .thenReturn(new LongResult(-1L));

        OrdersResult result = gOS.findOrders("", 0, 0);
        assertFalse(result.hasError());
        assertEquals(new OrdersResult(new Page<>(new ArrayList<>(), 0, 0, -1)), result);
    }

    @Test
    public void postTest() {
        when(oR.create(any()))
        .thenReturn(new OrderResult(new Order(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-05"), 100, 10000000, OrderStatus.RECEIVED)));

        OrderResult result = poOS.createOrder(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-05"), 100, 10000000, OrderStatus.RECEIVED));
        assertFalse(result.hasError());
        assertEquals(new OrderResult(new Order(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), LocalDate.parse("2026-01-05"), 100, 10000000, OrderStatus.RECEIVED)), result);
    }

    @Test
    public void putEmpty404Test() {
        when(oR.getOrderById(any()))
        .thenReturn(new OrderResult());

        NoContentResult result = puOS.updateOrder(ID, new OrderDTO());
        assertTrue(result.hasError());
        assertEquals(404, result.getErrorCode());
        assertEquals("NotFound", result.getMessage());
    }

    @Test
    public void putError500Test() {
        when(oR.getOrderById(any()))
        .thenReturn(new OrderResult(true));

        NoContentResult result = puOS.updateOrder(ID, new OrderDTO());
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }

    @Test
    public void putTest() {
        when(oR.update(any(), any()))
        .thenReturn(new NoContentResult());
        when(oR.getOrderById(any()))
        .thenReturn(new OrderResult(new Order()));
        NoContentResult result = puOS.updateOrder(ID, new OrderDTO());
        assertFalse(result.hasError());
        assertEquals(new NoContentResult(), result);
    }
}