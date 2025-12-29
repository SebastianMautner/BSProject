package sys.bac.application.port.in;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;

public interface DeleteCustomerUseCase {

    NoContentResult deleteCustomer(LongId id);
}
