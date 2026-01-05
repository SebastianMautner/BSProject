package sys.bac.application.domain.results.customer;

import java.util.Objects;

import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.SingleResult;

public class CustomerResult extends SingleResult<Customer> {
    
    public CustomerResult() {
    }

    public CustomerResult(Customer result) {
        super(result);
    }

    public CustomerResult(boolean error, int errorCode) {
        super();
        this.result = new Customer();
        this.error = error;
        this.errorCode = errorCode;
    }

    public CustomerResult(boolean error) {
        super();
        this.result = new Customer();
        this.error = error;
    }

    @Override
    public boolean equals(Object dto) {
        if (this == dto) return true;
        if(!(dto instanceof CustomerResult)) return false;
        CustomerResult dto2 = (CustomerResult) dto;
        return Objects.equals(this.result, dto2.result) &&
        error == dto2.error &&
        errorCode == dto2.errorCode &&
        errorMessage == dto2.errorMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, error, errorCode, errorMessage);
    }
}
