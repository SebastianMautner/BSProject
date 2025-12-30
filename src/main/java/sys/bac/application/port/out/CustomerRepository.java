package sys.bac.application.port.out;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.domain.results.customer.JpaCustomersResult;

public interface CustomerRepository {

    JpaCustomersResult getAllCustomers(String query, int offset, int size);

    CustomerResult create(Customer customer);

    CustomerResult getCustomerById(LongId id);

    NoContentResult delete(LongId id);

    NoContentResult update(LongId id, Customer customer);

    LongResult count();
}
