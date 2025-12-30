package sys.bac.application.domain.customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.port.in.customer.DeleteCustomerUseCase;
import sys.bac.application.port.out.CustomerRepository;

@ApplicationScoped
public class DeleteCustomerService implements DeleteCustomerUseCase{
    
    @Inject
    private CustomerRepository customerRepo;

    public NoContentResult deleteCustomer(LongId id) {
        CustomerResult extists = customerRepo.getCustomerById(id);
        NoContentResult result = new NoContentResult();
        
        if (extists.isEmpty()) {
            result.setError(404, "NotFound");
        } else if (extists.hasError()) {
            result.setError(500, extists.getMessage());
        } else {
            result = customerRepo.delete(id);
        }
        return result;
    }
}
