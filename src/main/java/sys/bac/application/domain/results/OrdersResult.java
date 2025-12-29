package sys.bac.application.domain.results;

import java.util.List;
import sys.bac.application.domain.models.order.Order;

public class OrdersResult extends MultiResult<Order> {

    public OrdersResult() {
    }

    public OrdersResult(List<Order> result) {
        super(result);
    }
}
