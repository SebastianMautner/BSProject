package sys.bac.adapters.in.api.models;

import java.util.List;
import java.util.Objects;

public class OrdersApiResult {
    private List<OrderDTO> result;

    private boolean next;

    private boolean prev;

    public OrdersApiResult() {
    }

    public OrdersApiResult(List<OrderDTO> result, boolean next, boolean prev) {
        this.result = result;
        this.next = next;
        this.prev = prev;
    }

    public List<OrderDTO> getResult() {
        return result;
    }

    public void setResult(List<OrderDTO> result) {
        this.result = result;
    }

    public boolean next() {
        return next;
    }

    public boolean prev() {
        return prev;
    }

    @Override
    public boolean equals(Object res) {
        if (this == res) return true;
        if (!(res instanceof OrdersApiResult)) return false;     
        OrdersApiResult result = (OrdersApiResult) res;
        return Objects.equals(this.result, result.result) &&
        this.next == result.next &&
        this.prev == result.prev;  
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, next, prev);
    }
}
