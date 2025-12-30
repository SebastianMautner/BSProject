package sys.bac.application.port.in.customer;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;

public interface DeleteCustomerUseCase {

    public NoContentResult deleteCustomer(LongId id);
}
