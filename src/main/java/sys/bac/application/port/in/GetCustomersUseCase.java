package sys.bac.application.port.in;

import java.util.List;

import sys.bac.adapters.in.api.models.CustomerDTO;

public interface GetCustomersUseCase {
    List<CustomerDTO> findCustomers();
}
