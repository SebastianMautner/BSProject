package sys.bac.application.port.in;

import sys.bac.application.domain.results.CustomerResult;
import sys.bac.application.domain.models.LongId;

public interface GetCustomerByIdUseCase {
    CustomerResult loadCustomerById(LongId cId);
}
