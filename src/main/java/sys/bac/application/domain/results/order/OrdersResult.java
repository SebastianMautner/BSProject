package sys.bac.application.domain.results.order;

import java.util.List;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.MultiResult;

public class OrdersResult extends MultiResult<Order> {

    public OrdersResult() {
    }

    public OrdersResult(List<Order> result) {
        super(result);
    }
}
