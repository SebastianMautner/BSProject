package sys.bac.adapters.in.api.models;

import java.util.List;

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
}
