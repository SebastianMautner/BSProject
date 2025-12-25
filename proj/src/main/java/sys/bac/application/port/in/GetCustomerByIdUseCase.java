package sys.bac.application.port.in;

import java.util.Optional;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;

public interface GetCustomerByIdUseCase {
    Optional<CustomerDTO> loadCustomerById(LongId cId);
}
