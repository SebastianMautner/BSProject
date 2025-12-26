package sys.bac.application.port.in;

import sys.bac.application.domain.models.customer.Customer;

public interface PutCustomerUseCase {
    public void updateCustomer(Customer customer);
}
