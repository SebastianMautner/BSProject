package sys.bac.application.domain.results.order;


import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.MultiResult;

public class OrdersResult extends MultiResult<Order>{
    
    public OrdersResult(Page<Order> orders) {
        super(orders);
    }

    public OrdersResult() {}
}
