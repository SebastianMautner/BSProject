package sys.bac.application.domain.results;


import sys.bac.application.domain.models.customer.Customer;

public class CustomerResult extends SingleResult<Customer> {
    
    public CustomerResult() {
    }

    public CustomerResult(Customer result) {
        super(result);
    }
}
