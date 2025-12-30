package sys.bac.application.port.out;

import java.util.List;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.OrderResult;

public interface OrderRepository {

    List<Order> getAllOrders();

    NoContentResult create(Order order);

    OrderResult getOrderById(LongId id);

    NoContentResult delete(LongId id);

    void update(LongId id, Order order);
}
