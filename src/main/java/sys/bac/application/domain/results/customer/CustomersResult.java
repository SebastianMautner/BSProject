package sys.bac.application.domain.results.customer;

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
}
