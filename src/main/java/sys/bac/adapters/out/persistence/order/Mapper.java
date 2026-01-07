package sys.bac.adapters.out.persistence.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import sys.bac.adapters.out.persistence.customer.CustomerJPAEntity;
import sys.bac.adapters.out.persistence.device.DeviceJPAEntity;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.order.OrderResult;

@ApplicationScoped
public class Mapper {

    @Inject 
    EntityManager eM;
    
    public OrderJPAEntity toJPA(Order order) {
        OrderJPAEntity e = new OrderJPAEntity();
        e.setIssueNotes(order.getIssueNotes());
        e.setReceivedAt(order.getReceivedAt());
        e.setCompletion(order.getCompletion());
        e.setCostEstimation(order.getCostEstimation());
        e.setFinalCost(order.getFinalCost());
        e.setStatus(order.getStatus());

        CustomerJPAEntity customer = eM.find(CustomerJPAEntity.class, order.getCustomerId());
        if(customer == null) throw new IllegalArgumentException("No Customer with Id" + order.getCustomerId());
        e.setCustomer(customer);
        DeviceJPAEntity device = eM.find(DeviceJPAEntity.class, order.getDeviceId());
        if(device == null) throw new IllegalArgumentException("No Device with Id" + order.getCustomerId());
        e.setDevice(device);
        return e;
    }

    public Order toOrder(OrderJPAEntity e) {
        return new Order(
                new LongId(e.getId()),
                e.getCustomer().getId(),
                e.getDevice().getId(),
                e.getIssueNotes(),
                e.getReceivedAt(),
                e.getCompletion(),
                e.getCostEstimation(),
                e.getFinalCost(),
                e.getStatus()
        );
    }

    public OrderResult toOrderResult(Order o) {
        if (o == null) {
            return new OrderResult();
        }
        return new OrderResult(o);
    }

    public OrderResult toOrderResult(OrderJPAEntity o) {
        return toOrderResult(toOrder(o));
    }
}
