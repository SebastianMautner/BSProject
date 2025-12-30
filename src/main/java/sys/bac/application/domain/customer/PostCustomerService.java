package sys.bac.application.domain.customer;

import sys.bac.adapters.in.api.adapter.customer.Mapper;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.port.in.customer.PostCustomerUseCase;
import sys.bac.application.port.out.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PostCustomerService implements PostCustomerUseCase{

    private final Mapper mapper = new Mapper();

    @Inject
    private CustomerRepository customerRepo;

    public CustomerResult createCustomer(CustomerDTO customer) {
        return customerRepo.create(mapper.toCustomer(customer));
    }
}
