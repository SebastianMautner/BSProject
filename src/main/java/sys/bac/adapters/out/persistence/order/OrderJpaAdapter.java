package sys.bac.adapters.out.persistence.order;

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
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.OrderResult;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class OrderJpaAdapter implements OrderRepository {

    private final Mapper mapper = new Mapper();

    @Inject
    private EntityManager eM;

    @Override
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
            throw new RuntimeException("WIP", e);
        }
        return list;
    }

    @Override
    public OrderResult getOrderById(LongId id) {
        OrderResult result = new OrderResult();
        try {
            OrderJPAEntity entity = eM.find(OrderJPAEntity.class, id.getId());
            if (entity == null) {
                result.setError(404, "Order not found");
                return result;
            }
            result = new OrderResult(mapper.toOrder(entity));
        } catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }

    @Transactional
    @Override
    public NoContentResult create(Order order) {
        NoContentResult result = new NoContentResult();
        try {
            OrderJPAEntity entity = mapper.toJPA(order);
            eM.persist(entity);
            result.setId(entity.getId());
        } catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }

    @Transactional
    @Override
    public NoContentResult delete(LongId id) {
        NoContentResult result = new NoContentResult();
        try {
            OrderJPAEntity entity = eM.find(OrderJPAEntity.class, id.getId());
            if (entity == null) {
                result.setError(404, "Order not found");
                return result;
            }
            eM.remove(entity);
        } catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }

    @Transactional
    @Override
    public void update(LongId id, Order order) {
        OrderJPAEntity entity = eM.find(OrderJPAEntity.class, id.getId());
        if (entity == null) {
            throw new IllegalArgumentException("Order not found: " + id.getId());
        }

        entity.setCustomerId(order.getCustomerId());
        entity.setSerialNumber(order.getSerialNumber());
        entity.setIssueNotes(order.getIssueNotes());
        entity.setReceivedAt(order.getReceivedAt());
        entity.setCompletion(order.getCompletion());
        entity.setCostEstimation(order.getCostEstimation());
        entity.setFinalCost(order.getFinalCost());
        entity.setStatus(order.getStatus());
    }
}
