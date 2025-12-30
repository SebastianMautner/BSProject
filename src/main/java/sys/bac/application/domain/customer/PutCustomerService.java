package sys.bac.application.domain.customer;

import sys.bac.adapters.in.api.adapter.customer.Mapper;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.port.in.customer.PutCustomerUseCase;
import sys.bac.application.port.out.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PutCustomerService implements PutCustomerUseCase{

    private Mapper mapper = new Mapper();

    @Inject
    private CustomerRepository customerRepo;

    public NoContentResult updateCustomer(LongId id, CustomerDTO customer) {
        CustomerResult exists = customerRepo.getCustomerById(id);
        NoContentResult result = new NoContentResult();
        
        if (exists.isEmpty()) {
            result.setError(404, "NotFound");
        } else if (exists.hasError()) {
            result.setError(500, exists.getMessage());
        } else {
            result = customerRepo.update(id, mapper.toCustomer(customer));
        }
        return result;
    }
}
