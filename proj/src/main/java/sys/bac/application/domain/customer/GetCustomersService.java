package sys.bac.application.domain.customer;

import java.util.List;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.port.in.GetCustomersUseCase;
import sys.bac.application.port.out.CustomerRepository;

public class GetCustomersService implements GetCustomersUseCase{ // might unify the Services in to one class
    
    private final CustomerRepository customerRepo;

    public GetCustomersService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public List<CustomerDTO> findCustomers(String query) {
        return customerRepo.getCustomers(query);
    }
}   
