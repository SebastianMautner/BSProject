package sys.bac.application.domain.results.order;


import java.util.Objects;

import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.MultiResult;

public class OrdersResult extends MultiResult<Order>{
    
    public OrdersResult(Page<Order> orders) {
        super(orders);
    }

    public OrdersResult() {}

    public OrdersResult(Page<Order> order, boolean error) {
        super();
        this.error = error;
    } 

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof OrdersResult)) return false;
        OrdersResult res = (OrdersResult) o;
        return Objects.equals(this.result, res.result) &&
        this.error == res.error &&
        this.errorCode == res.errorCode &&
        this.errorMessage == res.errorMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, error, errorCode, errorMessage);
    }
}
