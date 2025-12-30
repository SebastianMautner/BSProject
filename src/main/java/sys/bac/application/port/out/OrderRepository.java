package sys.bac.application.port.out;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.domain.results.order.OrdersResult;

public interface OrderRepository {

    OrdersResult getAllOrders(String query);

    OrderResult create(Order order);

    OrderResult getOrderById(LongId id);

    NoContentResult delete(LongId id);

    NoContentResult update(LongId id, Order order);
}
