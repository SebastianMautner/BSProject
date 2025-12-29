package sys.bac.application.domain.customer;

import sys.bac.adapters.in.api.adapter.customer.Mapper;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.port.in.PutCustomerUseCase;
import sys.bac.application.port.out.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PutCustomerService implements PutCustomerUseCase{

    private Mapper mapper = new Mapper();

    @Inject
    private CustomerRepository customerRepo;

    public void updateCustomer(long id, CustomerDTO customer) {
        customerRepo.update(new LongId(id), mapper.toCustomer(customer)); // check ids
    }
}
