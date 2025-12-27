package sys.bac.application.port.in;

import sys.bac.adapters.in.api.models.CustomerDTO;

public interface PutCustomerUseCase {
    public void updateCustomer(CustomerDTO customer);
}
