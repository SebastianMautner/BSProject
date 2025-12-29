package sys.bac.application.domain.results;

import sys.bac.application.domain.models.order.Order;

public class OrderResult extends SingleResult<Order> {

    public OrderResult() {
    }

    public OrderResult(Order result) {
        super(result);
    }
}
