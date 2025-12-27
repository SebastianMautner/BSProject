package sys.bac.application.domain.customer;

import java.util.Optional;

import jakarta.ws.rs.NotFoundException;
import sys.bac.adapters.in.api.adapter.customer.Mapper;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.port.in.GetCustomerByIdUseCase;
import sys.bac.application.port.out.CustomerRepository;

public class GetCustomerService implements GetCustomerByIdUseCase{ // might unify the Services in to one class
    
    private final Mapper mapper;

    private final CustomerRepository customerRepo;

    public GetCustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
        this.mapper = new Mapper();
    }

    public CustomerDTO loadCustomerById(LongId cId) {
        Optional<Customer> customer = customerRepo.getCustomerById(cId);
        if(customer.isPresent()) {
            return mapper.toDTO(customerRepo.getCustomerById(cId).get());
        }
        else {
            throw new NotFoundException("No Customer");
        }
    }
}
