package sys.bac.application.port.in.customer;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.customer.CustomerResult;

public interface GetCustomerByIdUseCase {
    public CustomerResult loadCustomerById(LongId cId);
}
