package sys.bac.application.domain.customer;

import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.port.in.PostCustomerUseCase;
import sys.bac.application.port.out.CustomerRepository;

public class PostCustomerService implements PostCustomerUseCase{
    private final CustomerRepository customerRepo;

    public PostCustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public void createCustomer(Customer customer) {
        customerRepo.create(customer);
    }
}
