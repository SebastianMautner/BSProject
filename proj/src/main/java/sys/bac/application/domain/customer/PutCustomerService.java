package sys.bac.application.domain.customer;

import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.port.in.PutCustomerUseCase;
import sys.bac.application.port.out.CustomerRepository;

public class PutCustomerService implements PutCustomerUseCase{
    private final CustomerRepository customerRepo;

    public PutCustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public void updateCustomer(Customer customer) {
        customerRepo.update(customer);
    }
}
