package sys.bac.application.port.in;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.results.NoContentResult;

public interface PostCustomerUseCase {
    
    public NoContentResult createCustomer(CustomerDTO customer);
}
