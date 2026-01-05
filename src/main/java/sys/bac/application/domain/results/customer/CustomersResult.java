package sys.bac.application.domain.results.customer;

import java.util.Objects;

import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.MultiResult;

public class CustomersResult extends MultiResult<Customer>{

    public CustomersResult(Page<Customer> customers) {
        super(customers);
    }

    public CustomersResult() {
    }

    public CustomersResult(Page<Customer> customer, boolean error) {
        super();
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof CustomersResult)) return false;
        CustomersResult res = (CustomersResult) o;
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
