package sys.bac.application.domain.customer;

import java.util.List;
import java.util.stream.Collectors;
import sys.bac.adapters.in.api.adapter.customer.Mapper;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.port.in.GetCustomersUseCase;
import sys.bac.application.port.out.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetCustomersService implements GetCustomersUseCase{ // might unify the Services in to one class

    @Inject
    private CustomerRepository customerRepo;

    private Mapper mapper = new Mapper();

    public GetCustomersService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
        mapper = new Mapper();
    }

    public List<CustomerDTO> findCustomers(String query) {
        return customerRepo.getAllCustomers(query).stream().map(mapper::toDTO).collect(Collectors.toList());
    }
}   
