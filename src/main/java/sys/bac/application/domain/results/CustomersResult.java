package sys.bac.application.domain.results;

import java.util.List;

import sys.bac.application.domain.models.customer.Customer;

public class CustomersResult extends MultiResult<Customer>{

    public CustomersResult(List<Customer> customers) {
        super(customers);
    }

    public CustomersResult() {
    }
}
