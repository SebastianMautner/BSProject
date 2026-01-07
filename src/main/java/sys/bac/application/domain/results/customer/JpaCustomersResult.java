package sys.bac.application.domain.results.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof JpaCustomersResult)) return false;
        JpaCustomersResult res = (JpaCustomersResult) o;
        return Objects.equals(this.results, res.results) &&
        this.error == res.error &&
        this.errorCode == res.errorCode &&
        this.errorMessage == res.errorMessage;
    }

    @Override 
    public int hashCode() {
        return Objects.hash(results, error, errorCode, errorMessage);
    }
}
