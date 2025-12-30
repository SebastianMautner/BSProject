package sys.bac.application.domain.results.customer;

import java.util.List;

import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.MultiResult;

public class CustomersResult extends MultiResult<Customer>{

    public CustomersResult(List<Customer> customers) {
        super(customers);
    }

    public CustomersResult() {
    }
}
