package sys.bac.adapters.out.persistence.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.transaction.Transactional;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.domain.results.customer.JpaCustomersResult;
import sys.bac.application.port.out.CustomerRepository;

@ApplicationScoped
public class CustomerJpaAdapter implements CustomerRepository{
    
    private final Mapper mapper = new Mapper();
    
    @Inject
    private EntityManager eM;
    
    @Transactional
    public CustomerResult create(Customer customer) {
        CustomerResult result = new CustomerResult();
        try {
            CustomerJPAEntity c = mapper.toJPA(customer);
            eM.persist(c);
            eM.flush();
            result.setResult(mapper.toCustomer(c));
        }
        catch(Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
    
    public JpaCustomersResult getAllCustomers(String query, int offset, int size) {
        List<Customer> list = new ArrayList<>();
        JpaCustomersResult result =  new JpaCustomersResult();
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<CustomerJPAEntity> cQ = cB.createQuery(CustomerJPAEntity.class);
            Root<CustomerJPAEntity> root = cQ.from(CustomerJPAEntity.class);
            if (!query.isBlank()) {
                List<Predicate> predicates = new ArrayList<>();
                EntityType<CustomerJPAEntity> entityType = eM.getMetamodel().entity(CustomerJPAEntity.class);
                for (Attribute<? super CustomerJPAEntity, ?> attr : entityType.getAttributes()) {
                    
                    if(attr.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
                        Expression<String> expr;
                        if (attr.getJavaType().equals(String.class)) {
                            expr = cB.lower(root.get(attr.getName()));
                        } else {
                            expr = cB.lower(cB.toString(root.get(attr.getName())));
                        }
                        predicates.add(cB.like(expr, "%" + query.toLowerCase() + "%"));
                    }
                }

                if(!predicates.isEmpty()) {
                    cQ.where(cB.or(predicates.toArray(new Predicate[0])));
                }
            }
            
            cQ.orderBy(cB.asc(root.get("id")));
            list = eM.createQuery(cQ)
            .setFirstResult(offset)
            .setMaxResults(size)
            .getResultList()
            .stream()
            .map(mapper::toCustomer)
            .collect(Collectors.toList());
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
    public NoContentResult update(LongId id, Customer customer) {
        NoContentResult result = new NoContentResult();
        try {
            CustomerJPAEntity c = eM.find(CustomerJPAEntity.class, id.getId());
            c.setSurname(customer.getSurname());
            c.setFirstName(customer.getName());
            c.setEMail(customer.getEMail());
            c.setPhone(customer.getPhone());
        }
        catch(Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
    
    public LongResult count(String query) {
        LongResult result = new LongResult();
        long amount = -1;
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<Long> cQ = cB.createQuery(Long.class);
            Root<CustomerJPAEntity> root = cQ.from(CustomerJPAEntity.class);
            
            if (!query.isBlank()) {
                List<Predicate> predicates = new ArrayList<>();
                EntityType<CustomerJPAEntity> entityType = eM.getMetamodel().entity(CustomerJPAEntity.class);
                for (Attribute<? super CustomerJPAEntity, ?> attr : entityType.getAttributes()) {
                    
                    if(attr.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
                        Expression<String> expr;
                        if (attr.getJavaType().equals(String.class)) {
                            expr = cB.lower(root.get(attr.getName()));
                        } else {
                            expr = cB.lower(cB.toString(root.get(attr.getName())));
                        }
                        predicates.add(cB.like(expr, "%" + query.toLowerCase() + "%"));
                    }
                }
                if(!predicates.isEmpty()) {
                    cQ.where(cB.or(predicates.toArray(new Predicate[0])));
                }
            }
            cQ.select(cB.count(root));
            amount = eM.createQuery(cQ).getSingleResult();
        }
        catch ( Exception e) {
            result.setError(500, e.getMessage());
        }
        result.setResult(amount);
        return result;
    }
}
