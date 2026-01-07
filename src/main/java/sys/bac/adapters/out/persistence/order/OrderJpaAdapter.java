package sys.bac.adapters.out.persistence.order;

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
import sys.bac.adapters.out.persistence.customer.CustomerJPAEntity;
import sys.bac.adapters.out.persistence.device.DeviceJPAEntity;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.order.JpaOrdersResult;
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class OrderJpaAdapter implements OrderRepository {
    
    @Inject
    private  Mapper mapper;
    
    @Inject
    private EntityManager eM;
    
    public JpaOrdersResult getAllOrders(String query, int offset, int size) {
        List<Order> list = new ArrayList<>();
        JpaOrdersResult result =  new JpaOrdersResult();
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<OrderJPAEntity> cQ = cB.createQuery(OrderJPAEntity.class);
            Root<OrderJPAEntity> root = cQ.from(OrderJPAEntity.class);
            if (!query.isBlank()) {
                List<Predicate> predicates = new ArrayList<>();
                EntityType<OrderJPAEntity> entityType = eM.getMetamodel().entity(OrderJPAEntity.class);
                for (Attribute<? super OrderJPAEntity, ?> attr : entityType.getAttributes()) {
                    
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
            .map(mapper::toOrder)
            .collect(Collectors.toList());
        }
        catch ( Exception e) {
            result.setError(500, e.getMessage());
        }
        result.setResult(list);
        return result;
    }
    
    @Override
    public OrderResult getOrderById(LongId id) {
        OrderResult result = new OrderResult();
        if (id == null) {
            result.setError(500, "id is null.");
            return result;            
        }
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
        if (order == null) {
            result.setError(500, "Order is null");
            return result;
        }
        try {
            OrderJPAEntity entity = mapper.toJPA(order);
            eM.persist(entity);
            eM.flush();
            result.setResult(mapper.toOrder(entity));
        // } catch(IllegalArgumentException e) {
            // result.setError(422, "No matching entity ");
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
            entity.setCustomer(eM.getReference(CustomerJPAEntity.class, order.getCustomerId()));
            entity.setDevice(eM.getReference(DeviceJPAEntity.class, order.getDeviceId()));
            entity.setIssueNotes(order.getIssueNotes());
            entity.setReceivedAt(order.getReceivedAt());
            entity.setCompletion(order.getCompletion());
            entity.setCostEstimation(order.getCostEstimation());
            entity.setFinalCost(order.getFinalCost());
            entity.setStatus(order.getStatus());
        } 
        catch (Exception e) {
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
            Root<OrderJPAEntity> root = cQ.from(OrderJPAEntity.class);
            
            if (!query.isBlank()) {
                List<Predicate> predicates = new ArrayList<>();
                EntityType<OrderJPAEntity> entityType = eM.getMetamodel().entity(OrderJPAEntity.class);
                for (Attribute<? super OrderJPAEntity, ?> attr : entityType.getAttributes()) {
                    
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
