package sys.bac.application.port.out;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.order.JpaOrdersResult;
import sys.bac.application.domain.results.order.OrderResult;

public interface OrderRepository {

    JpaOrdersResult getAllOrders(String query, int offset, int size);

    JpaOrdersResult getAllOrders(String query, String status, int offset, int size);

    OrderResult create(Order order);

    OrderResult getOrderById(LongId id);

    NoContentResult delete(LongId id);

    NoContentResult update(LongId id, Order order);

    LongResult count(String query);

    LongResult count(String query, String status);
}
