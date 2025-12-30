package sys.bac.adapters.out.persistence.customer;

import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.customer.CustomerResult;

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

    public CustomerResult toCustomerResult(Customer c) {
        return new CustomerResult(c);
    }

    public CustomerResult toCustomerResult(CustomerJPAEntity c) {
        return toCustomerResult(toCustomer(c));
    }
}
