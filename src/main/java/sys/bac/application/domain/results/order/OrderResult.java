package sys.bac.application.domain.results.order;

import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.SingleResult;

public class OrderResult extends SingleResult<Order> {

    public OrderResult() {
    }

    public OrderResult(Order result) {
        super(result);
    }
}
