package sys.bac.adapters.out.persistence.order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import sys.bac.adapters.out.persistence.customer.CustomerJPAEntity;
import sys.bac.adapters.out.persistence.device.DeviceJPAEntity;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.models.order.OrderStatus;
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
        return getAllOrders(query, "", offset, size);
    }
    
    public JpaOrdersResult getAllOrders(String query, String status, int offset, int size) {
        List<Order> list = new ArrayList<>();
        JpaOrdersResult result =  new JpaOrdersResult();
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<OrderJPAEntity> cQ = cB.createQuery(OrderJPAEntity.class);
            Root<OrderJPAEntity> root = cQ.from(OrderJPAEntity.class);
            List<Predicate> predicates = buildSearchPredicates(cB, root, query, status);

            if(!predicates.isEmpty()) {
                cQ.where(cB.and(predicates.toArray(new Predicate[0])));
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
        return count(query, "");
    }

    public LongResult count(String query, String status) {
        LongResult result = new LongResult();
        long amount = -1;
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<Long> cQ = cB.createQuery(Long.class);
            Root<OrderJPAEntity> root = cQ.from(OrderJPAEntity.class);
            List<Predicate> predicates = buildSearchPredicates(cB, root, query, status);

            if(!predicates.isEmpty()) {
                cQ.where(cB.and(predicates.toArray(new Predicate[0])));
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

    private List<Predicate> buildSearchPredicates(CriteriaBuilder cB, Root<OrderJPAEntity> root, String query, String status) {
        List<Predicate> predicates = new ArrayList<>();
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        String normalizedStatus = status == null ? "" : status.trim().toUpperCase();

        if(!normalizedStatus.isBlank()) {
            try {
                OrderStatus statusFilter = OrderStatus.valueOf(normalizedStatus);
                predicates.add(cB.equal(root.get("status"), statusFilter));
            } catch (IllegalArgumentException exception) {
                predicates.add(cB.disjunction());
                return predicates;
            }
        }

        if(normalizedQuery.isBlank()) {
            return predicates;
        }

        Long customerId = extractExactId(normalizedQuery, "customer");
        if(customerId != null && normalizedQuery.startsWith("customer:")) {
            predicates.add(cB.equal(root.get("customer").get("customerId"), customerId));
            return predicates;
        }

        Long deviceId = extractExactId(normalizedQuery, "device");
        if(deviceId != null && normalizedQuery.startsWith("device:")) {
            predicates.add(cB.equal(root.get("device").get("id"), deviceId));
            return predicates;
        }

        Long exactId = extractExactId(normalizedQuery, "id");
        if(exactId != null) {
            predicates.add(cB.equal(root.get("id"), exactId));
            return predicates;
        }

        String pattern = "%" + normalizedQuery + "%";
        predicates.add(cB.or(
            cB.like(cB.lower(root.get("issueNotes")), pattern),
            cB.like(cB.lower(root.get("status").as(String.class)), pattern),
            cB.like(cB.lower(root.get("receivedAt").as(String.class)), pattern),
            cB.like(cB.lower(root.get("completion").as(String.class)), pattern)
        ));
        return predicates;
    }

    private Long extractExactId(String query, String prefix) {
        String normalized = query.trim().toLowerCase();
        String prefixed = prefix + ":";

        if(normalized.startsWith(prefixed)) {
            normalized = normalized.substring(prefixed.length()).trim();
        } else if(normalized.startsWith("#")) {
            normalized = normalized.substring(1).trim();
        }

        if(normalized.matches("\\d+")) {
            return Long.parseLong(normalized);
        }
        return null;
    }
}
