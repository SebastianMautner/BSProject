package sys.bac.application.port.in;

import sys.bac.application.domain.results.CustomerResult;

public interface GetCustomerByIdUseCase {
    public CustomerResult loadCustomerById(long cId);
}
