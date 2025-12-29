package sys.bac.application.port.in;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.CustomerResult;

public interface PutCustomerUseCase {
    public CustomerResult updateCustomer(LongId id, CustomerDTO customer);
}
