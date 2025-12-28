package sys.bac.application.port.in;

import sys.bac.adapters.in.api.models.CustomerDTO;

public interface GetCustomerByIdUseCase {
    public CustomerDTO loadCustomerById(long cId);
}
