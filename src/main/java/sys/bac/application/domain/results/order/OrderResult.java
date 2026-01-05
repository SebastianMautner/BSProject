package sys.bac.application.domain.results.order;

import java.util.Objects;

import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.SingleResult;

public class OrderResult extends SingleResult<Order> {

    public OrderResult() {
    }

    public OrderResult(Order result) {
        super(result);
    }

    public OrderResult(boolean error) {
        this.result = new Order();
        this.error = error;
    }
    public OrderResult(boolean error, int errorCode) {
        this.result = new Order();
        this.error = error;
        this.errorCode = errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (!(o instanceof OrderResult)) return false;
        OrderResult res = (OrderResult) o;
        return Objects.equals(this.result, res.result) &&
        this.error == res.error &&
        this.errorCode == res.errorCode &&
        this.errorMessage == res.errorMessage; 
    }
}
