package sys.bac.adapters.in.api.adapter.customer;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.customer.Customer;

public class Mapper {
    public Customer fromDTO(CustomerDTO cDTO) {
        return new Customer(cDTO.getId(), cDTO.getSurname(), cDTO.getName(), cDTO.getEMail(), cDTO.getPhone());
    }

    public CustomerDTO fromCustomer(Customer c) {
        return new CustomerDTO(c.getcustomerId(), c.getSurname(), c.getName(), c.getEMail(), c.getPhone());
    }
}
