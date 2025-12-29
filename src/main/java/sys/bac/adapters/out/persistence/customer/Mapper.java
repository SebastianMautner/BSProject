package sys.bac.adapters.out.persistence.customer;

import sys.bac.application.domain.models.customer.Customer;

public class Mapper {

    public CustomerJPAEntity toJPA(Customer c) {
        CustomerJPAEntity result = new CustomerJPAEntity();
        result.setSurname(c.getSurname());
        result.setFirstName(c.getName());
        result.setEMail(c.getEMail());
        result.setPhone(c.getPhone());
        return result;
    }

    public Customer toCustomer(CustomerJPAEntity c) {
        return new Customer(c.getId(), c.getSurname(), c.getFirstname(), c.getEMail(), c.getPhone());
    }
}
