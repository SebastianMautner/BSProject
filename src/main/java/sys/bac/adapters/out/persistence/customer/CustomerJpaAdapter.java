package sys.bac.adapters.out.persistence.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.CustomerResult;
import sys.bac.application.domain.results.CustomersResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.port.out.CustomerRepository;

@ApplicationScoped
public class CustomerJpaAdapter implements CustomerRepository{

    private Mapper mapper = new Mapper();

    @Inject
    private EntityManager eM;

    @Transactional
    public CustomerResult create(Customer customer) {
        CustomerResult result = new CustomerResult();
        try {
            CustomerJPAEntity c = mapper.toJPA(customer);
            eM.persist(c);
            result.setResult(mapper.toCustomer(c));
        }
        catch(Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }

    public CustomersResult getAllCustomers(String query) {
        List<Customer> list = new ArrayList<>();
        CustomersResult result =  new CustomersResult();
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<CustomerJPAEntity> cQ = cB.createQuery(CustomerJPAEntity.class);
            Root<CustomerJPAEntity> root = cQ.from(CustomerJPAEntity.class);
            cQ.select(root);
            list = eM.createQuery(cQ).getResultList().stream().map(customer -> mapper.toCustomer(customer)).collect(Collectors.toList());
        }
        catch ( Exception e) {
            result.setError(500, e.getMessage());
        }
        result.setResult(list);
        return result;
    }

    public CustomerResult getCustomerById(LongId id) {
        CustomerResult result = new CustomerResult();
        try {
            result = mapper.toCustomerResult(eM.find(CustomerJPAEntity.class, id.getId()));
        }
        catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }

    @Transactional
    public NoContentResult delete(LongId id) {
        NoContentResult result = new NoContentResult();
        try {
            eM.remove(eM.find(CustomerJPAEntity.class, id.getId()));
        }
        catch(Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }

    @Transactional
    public CustomerResult update(LongId id, Customer customer) {
        CustomerResult result = new CustomerResult();
        try {
            CustomerJPAEntity c = eM.find(CustomerJPAEntity.class, id.getId());
            eM.detach(c);
            c.setSurname(customer.getSurname());
            c.setFirstName(customer.getName());
            c.setEMail(customer.getEMail());
            c.setPhone(customer.getPhone());
            eM.merge(c);
            result.setResult(mapper.toCustomer(c));
        }
        catch(Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
}
