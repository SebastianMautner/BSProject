package sys.bac.adapters.in.api.models;

import java.util.List;
import java.util.Objects;

public class CustomersApiResult {
    private List<CustomerDTO> result;

    private boolean next;

    private boolean prev;

    public CustomersApiResult() {
    }

    public CustomersApiResult(List<CustomerDTO> result, boolean next, boolean prev) {
        this.result = result;
        this.next = next;
        this.prev = prev;
    }

    public List<CustomerDTO> getResult() {
        return result;
    }

    public void setResult(List<CustomerDTO> result) {
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
        if (!(res instanceof CustomersApiResult)) return false;     
        CustomersApiResult result = (CustomersApiResult) res;
        return Objects.equals(this.result, result.result) &&
        this.next == result.next &&
        this.prev == result.prev;  
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, next, prev);
    }
}
