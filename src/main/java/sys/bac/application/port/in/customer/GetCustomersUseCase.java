package sys.bac.application.port.in.customer;

import sys.bac.application.domain.results.customer.CustomersResult;

public interface GetCustomersUseCase {
    public CustomersResult findCustomers(String query, int offset, int size);
}
