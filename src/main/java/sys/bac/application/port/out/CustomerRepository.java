package sys.bac.application.port.out;

import java.util.List;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.CustomerResult;
import sys.bac.application.domain.results.NoContentResult;

public interface CustomerRepository {

    List<Customer> getAllCustomers(); //Query idk

    NoContentResult create(Customer customer);

    CustomerResult getCustomerById(LongId id);

    NoContentResult delete(LongId id);

    void update(LongId id, Customer customer); //maybe not void, idk
}
