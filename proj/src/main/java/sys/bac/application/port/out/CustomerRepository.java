package sys.bac.application.port.out;

import java.util.List;
import java.util.Optional;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;

public interface CustomerRepository {

    List<Customer> getCustomers(String query);

    void create(Customer customer);

    Optional<Customer> getCustomerById(LongId id);

    void delete(LongId id);

    void update(Customer customer); //maybe not void, idk
}
