package sys.bac.application.port.in;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;

public interface GetCustomerByIdUseCase {
    CustomerDTO loadCustomerById(LongId cId);
}
