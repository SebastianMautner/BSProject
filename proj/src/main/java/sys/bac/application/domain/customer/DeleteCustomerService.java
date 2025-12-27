package sys.bac.application.domain.customer;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.port.in.DeleteCustomerUseCase;
import sys.bac.application.port.out.CustomerRepository;

public class DeleteCustomerService implements DeleteCustomerUseCase{
    
    private final CustomerRepository customerRepo;

    public DeleteCustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public void deleteCustomer(long id) {
        customerRepo.delete(new LongId(id));
    }
}
