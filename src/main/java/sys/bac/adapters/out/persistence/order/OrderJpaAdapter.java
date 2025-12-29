package sys.bac.adapters.out.persistence.order;

import java.util.ArrayList;
import java.util.List;
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
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.OrderResult;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class OrderJpaAdapter implements OrderRepository {

    private Mapper mapper = new Mapper();

    @Inject
    private EntityManager eM;

    @Transactional
    public NoContentResult create(Order order) {
        long id = -1;
        NoContentResult result = new NoContentResult();
        try {
            OrderJPAEntity o = mapper.toJPA(order);
            eM.persist(o);
            id = o.getId();
        } catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        result.setId(id);
        return result;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<OrderJPAEntity> cQ = cB.createQuery(OrderJPAEntity.class);
            Root<OrderJPAEntity> root = cQ.from(OrderJPAEntity.class);
            cQ.select(root);
            list = eM.createQuery(cQ)
                    .getResultList()
                    .stream()
                    .map(mapper::toOrder)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("WIP"); // analog zu deinem Customer-Code (dort steht aktuell auch WIP)
        }
        return list;
    }

    public OrderResult getOrderById(LongId id) {
        OrderResult result = new OrderResult();
        try {
            result = mapper.toOrderResult(eM.find(OrderJPAEntity.class, id.getId()));
        } catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }

    public NoContentResult delete(LongId id) {
        NoContentResult result = new NoContentResult();
        try {
            EntityTransaction eT = eM.getTransaction();
            eT.begin();
            eM.remove(eM.find(OrderJPAEntity.class, id.getId()));
            eT.commit();
        } catch (Exception e) {
            result.setError(500, "Internal Server Error");
        }
        return result;
    }

    public void update(LongId id, Order order) {
        try {
            EntityTransaction eT = eM.getTransaction();
            eT.begin();
            OrderJPAEntity o = eM.find(OrderJPAEntity.class, id.getId());
            eM.detach(o);
            o.setNote(order.getNote());
            eM.merge(o);
            eT.commit();
        } catch (Exception e) {
            throw new RuntimeException("WIP");
        }
    }
}
