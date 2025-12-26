package sys.bac.application.port.in;

import sys.bac.application.domain.models.customer.Customer;

public interface PostCustomerUseCase {
    public void createCustomer(Customer customer);
}
