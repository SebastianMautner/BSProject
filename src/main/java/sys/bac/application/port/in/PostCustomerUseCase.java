package sys.bac.application.port.in;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.results.CustomerResult;

public interface PostCustomerUseCase {
    
    public CustomerResult createCustomer(CustomerDTO customer);
}
