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
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.domain.results.order.OrdersResult;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class OrderJpaAdapter implements OrderRepository {
    
    private final Mapper mapper = new Mapper();
    
    @Inject
    private EntityManager eM;
    
    public OrdersResult getAllOrders(String query) {
        List<Order> list = new ArrayList<>();
        OrdersResult result = new OrdersResult();
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
            result.setError(500, e.getMessage());
        }
        result.setResult(list);
        return result;
    }
    
    @Override
    public OrderResult getOrderById(LongId id) {
        OrderResult result = new OrderResult();
        try {
            result = mapper.toOrderResult(eM.find(OrderJPAEntity.class, id.getId()));
        } 
        catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
    
    @Transactional
    @Override
    public OrderResult create(Order order) {
        OrderResult result = new OrderResult();
        try {
            OrderJPAEntity entity = mapper.toJPA(order);
            eM.persist(entity);
            result.setResult(mapper.toOrder(entity));
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
            eM.remove(eM.find(OrderJPAEntity.class, id.getId()));
        } 
        catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
    
    @Transactional
    @Override
    public NoContentResult update(LongId id, Order order) {
        NoContentResult result = new NoContentResult();
        try {
            OrderJPAEntity entity = eM.find(OrderJPAEntity.class, id.getId());
            eM.detach(entity);
            entity.setCustomerId(order.getCustomerId());
            entity.setDeviceId(order.getDeviceId());
            entity.setIssueNotes(order.getIssueNotes());
            entity.setReceivedAt(order.getReceivedAt());
            entity.setCompletion(order.getCompletion());
            entity.setCostEstimation(order.getCostEstimation());
            entity.setFinalCost(order.getFinalCost());
            entity.setStatus(order.getStatus());
            eM.merge(entity);
        } 
        catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
}
