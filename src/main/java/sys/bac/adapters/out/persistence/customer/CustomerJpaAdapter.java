package sys.bac.adapters.out.persistence.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.port.out.CustomerRepository;

@ApplicationScoped
public class CustomerJpaAdapter implements CustomerRepository{

    private Mapper mapper = new Mapper();

    @Inject
    private EntityManager eM;

    @Transactional
    public NoContentResult create(Customer customer) {
        long id = -1;
        NoContentResult result = new NoContentResult();
        try {
            CustomerJPAEntity c = mapper.toJPA(customer);
            eM.persist(c);
            id = c.getId();
        }
        catch(Exception e) {
            result.setError(500, e.getMessage());
        }
        result.setId(id);
        return result;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<CustomerJPAEntity> cQ = cB.createQuery(CustomerJPAEntity.class);
            Root<CustomerJPAEntity> root = cQ.from(CustomerJPAEntity.class);
            cQ.select(root);
            list = eM.createQuery(cQ).getResultList().stream().map(result -> mapper.toCustomer(result)).collect(Collectors.toList());
        }
        catch ( Exception e) {
            throw new RuntimeException("FUCK"); //WIP
        }
        return list;
    }

    public Optional<Customer> getCustomerById(LongId id) {
        Optional<Customer> result;
        try {
            EntityTransaction eT = eM.getTransaction();
            eT.begin();
            result = Optional.of(mapper.toCustomer(eM.find(CustomerJPAEntity.class, id.getId())));
            eT.commit();
        }
        catch ( Exception e) {
            throw new RuntimeException("FUCK"); //WIP
        }
        return result;
    }

    public NoContentResult delete(LongId id) {
        NoContentResult result = new NoContentResult();
        try {
            EntityTransaction eT = eM.getTransaction();
            eT.begin();
            eM.remove(eM.find(CustomerJPAEntity.class, id.getId()));
            eT.commit();
        }
        catch(Exception e) {
            result.setError(500, "Internal Server Error");
        }
        return result;
    }

    public void update(LongId id, Customer customer) {
        try {
            EntityTransaction eT = eM.getTransaction();
            eT.begin();
            CustomerJPAEntity c = eM.find(CustomerJPAEntity.class, id.getId());
            eM.detach(c);
            c.setSurname(customer.getSurname());
            c.setFirstName(customer.getName());
            c.setEMail(customer.getEMail());
            c.setPhone(customer.getPhone());
            eM.merge(c);
            eT.commit();
        }
        catch(Exception e) {
            throw new RuntimeException("FUCK"); //WIP
        }
    }
}
