package sys.bac.adapters.out.persistence.customer;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
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
            eM.persist(mapper.fromCustomer(customer));
            eT.commit();
        }
        catch(Exception e) {
            throw new RuntimeException("FUCK"); //WIP
        }
    }

    public List<Customer
}
