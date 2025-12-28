package sys.bac.application.port.in;

import sys.bac.adapters.in.api.models.CustomerDTO;

public interface PostCustomerUseCase {
    public void createCustomer(CustomerDTO customer);
}
