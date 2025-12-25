package sys.bac.application.domain.customer;

import java.util.Optional;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.port.in.GetCustomerByIdUseCase;
import sys.bac.application.port.out.CustomerRepository;

public class GetCustomerService implements GetCustomerByIdUseCase{ // might unify the Services in to one class
    
    private final CustomerRepository customerRepo;

    public GetCustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public Optional<CustomerDTO> loadCustomerById(LongId cId) {
        return customerRepo.getCustomerById(cId);
    }
}
