package sys.bac.application.domain.customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.port.in.customer.GetCustomerByIdUseCase;
import sys.bac.application.port.out.CustomerRepository;

@ApplicationScoped
public class GetCustomerService implements GetCustomerByIdUseCase{
    
    @Inject
    private CustomerRepository customerRepo;

    public CustomerResult loadCustomerById(LongId cId) {
        return customerRepo.getCustomerById(cId);
    }
}
