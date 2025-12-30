package sys.bac.application.domain.results.customer;


import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.SingleResult;

public class CustomerResult extends SingleResult<Customer> {
    
    public CustomerResult() {
    }

    public CustomerResult(Customer result) {
        super(result);
    }
}
