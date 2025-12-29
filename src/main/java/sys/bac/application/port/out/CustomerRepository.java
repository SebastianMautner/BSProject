package sys.bac.application.port.out;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.CustomerResult;
import sys.bac.application.domain.results.CustomersResult;
import sys.bac.application.domain.results.NoContentResult;

public interface CustomerRepository {

    CustomersResult getAllCustomers(String query); //Query idk

    CustomerResult create(Customer customer);

    CustomerResult getCustomerById(LongId id);

    NoContentResult delete(LongId id);

    CustomerResult update(LongId id, Customer customer);
}
