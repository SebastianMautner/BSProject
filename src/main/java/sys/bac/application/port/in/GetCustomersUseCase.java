package sys.bac.application.port.in;

import sys.bac.application.domain.results.CustomersResult;

public interface GetCustomersUseCase {
    CustomersResult findCustomers();
}
