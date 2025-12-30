package sys.bac.application.domain.customer;

import sys.bac.application.domain.results.customer.CustomersResult;
import sys.bac.application.port.in.customer.GetCustomersUseCase;
import sys.bac.application.port.out.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetCustomersService implements GetCustomersUseCase{ // might unify the Services in to one class

    @Inject
    private CustomerRepository customerRepo;

    public CustomersResult findCustomers(String query) {
        return customerRepo.getAllCustomers(query);
    }
}   
