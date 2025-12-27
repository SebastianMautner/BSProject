package sys.bac.adapters.out.persistence.customer;

import sys.bac.application.domain.models.customer.Customer;

public class Mapper {

    public CustomerJPAEntity toJPA(Customer c) {
        return new CustomerJPAEntity(c.getcustomerId(), c.getSurname(), c.getName(), c.getEMail(), c.getPhone());
    }

    public Customer toCustomer(CustomerJPAEntity c) {
        return new Customer(c.getId(), c.getSurname(), c.getFirstname(), c.getEMail(), c.getPhone());
    }
}
