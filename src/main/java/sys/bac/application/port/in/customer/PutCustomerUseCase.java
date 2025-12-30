package sys.bac.application.port.in.customer;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;

public interface PutCustomerUseCase {
    public NoContentResult updateCustomer(LongId id, CustomerDTO customer);
}
