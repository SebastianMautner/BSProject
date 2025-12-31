package sys.bac.application.domain.customer;

import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.customer.CustomersResult;
import sys.bac.application.domain.results.customer.JpaCustomersResult;
import sys.bac.application.port.in.customer.GetCustomersUseCase;
import sys.bac.application.port.out.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetCustomersService implements GetCustomersUseCase{

    @Inject
    private CustomerRepository customerRepo;

    public CustomersResult findCustomers(String query, int offset, int size) {
        JpaCustomersResult jpaResult = customerRepo.getAllCustomers(query, offset, size);
        LongResult totalResult = customerRepo.count();
        
        CustomersResult result = new CustomersResult(new Page<>(jpaResult.getResult(), offset, size, totalResult.getResult()));
        if (jpaResult.hasError() || totalResult.hasError()) {
            result.setError(500, jpaResult.getMessage() + "\n" + totalResult.getMessage());
        }
        return result;
    }
}   
