package sys.bac.adapters.out.persistence.customer;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.customer.Customer;
import sys.bac.application.port.out.CustomerRepository;

public class CustomerJpaAdapter implements CustomerRepository{

    private final Mapper mapper;

    private final EntityManagerFactory eMF;

    public CustomerJpaAdapter(Mapper mapper, EntityManagerFactory entityManagerFactory) {
        this.mapper = mapper;
        this.eMF = entityManagerFactory;
    }

    public void create(Customer customer) {
        try (EntityManager eM = eMF.createEntityManager();){
            EntityTransaction eT = eM.getTransaction();
            eT.begin();
            eM.persist(mapper.toJPA(customer));
            eT.commit();
        }
        catch(Exception e) {
            throw new RuntimeException("FUCK"); //WIP
        }
    }

    public List<Customer> getCustomers(String query) {
        return null; // TODO
    }

    public Optional<Customer> getCustomerById(LongId id) {
        return null;// TODO
    }

    public void delete(LongId id) {
        //TODO
    }

    public void update(Customer customer) {
        //TODO
    }
}
