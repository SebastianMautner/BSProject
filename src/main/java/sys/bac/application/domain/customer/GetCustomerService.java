package sys.bac.application.domain.customer;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import sys.bac.adapters.in.api.adapter.customer.Mapper;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.port.in.GetCustomerByIdUseCase;
import sys.bac.application.port.out.CustomerRepository;

@ApplicationScoped
public class GetCustomerService implements GetCustomerByIdUseCase{ // might unify the Services in to one class
    
    private final Mapper mapper;

    @Inject
    private CustomerRepository customerRepo;

    public GetCustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
        this.mapper = new Mapper();
    }

    public CustomerDTO loadCustomerById(long cId) {
        Optional<Customer> customer = customerRepo.getCustomerById(new LongId(cId));
        if(customer.isPresent()) {
            return mapper.toDTO(customer.get());
        }
        else {
            throw new NotFoundException("No Customer");
        }
    }
}
