package sys.bac.application.domain.customer;

import sys.bac.adapters.in.api.adapter.customer.Mapper;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.port.in.PostCustomerUseCase;
import sys.bac.application.port.out.CustomerRepository;

public class PostCustomerService implements PostCustomerUseCase{

    private final Mapper mapper;
    private final CustomerRepository customerRepo;

    public PostCustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
        this.mapper = new Mapper();
    }

    public void createCustomer(CustomerDTO customer) {
        customerRepo.create(mapper.toCustomer(customer));
    }
}
