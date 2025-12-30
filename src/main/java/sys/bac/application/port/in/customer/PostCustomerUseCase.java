package sys.bac.application.port.in.customer;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.results.customer.CustomerResult;

public interface PostCustomerUseCase {
    
    public CustomerResult createCustomer(CustomerDTO customer);
}
