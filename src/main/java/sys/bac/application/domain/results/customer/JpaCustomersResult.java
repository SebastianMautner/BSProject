package sys.bac.application.domain.results.customer;

import java.util.ArrayList;
import java.util.List;

import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.JpaMultiResult;

public class JpaCustomersResult extends JpaMultiResult<Customer>{
    
    public JpaCustomersResult(List<Customer> customers) {
        super(customers);
    }

    public JpaCustomersResult() {}

    public JpaCustomersResult(boolean error, int errorCode) {
        super();
        this.results = new ArrayList<>();
        this.error = error;
        this.errorCode = errorCode;
    }

    public JpaCustomersResult(boolean error) {
        super();
        this.results = new ArrayList<>();
        this.error = error;
    }
}
