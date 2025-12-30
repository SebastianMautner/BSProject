package sys.bac.adapters.out.persistence.order;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.order.OrderResult;

public class Mapper {

    public OrderJPAEntity toJPA(Order order) {
        OrderJPAEntity e = new OrderJPAEntity();
        e.setCustomerId(order.getCustomerId());
        e.setDeviceId(order.getDeviceId());
        e.setIssueNotes(order.getIssueNotes());
        e.setReceivedAt(order.getReceivedAt());
        e.setCompletion(order.getCompletion());
        e.setCostEstimation(order.getCostEstimation());
        e.setFinalCost(order.getFinalCost());
        e.setStatus(order.getStatus());
        return e;
    }

    public Order toOrder(OrderJPAEntity e) {
        return new Order(
                new LongId(e.getId()),
                e.getCustomerId(),
                e.getDeviceId(),
                e.getIssueNotes(),
                e.getReceivedAt(),
                e.getCompletion(),
                e.getCostEstimation(),
                e.getFinalCost(),
                e.getStatus()
        );
    }

    public OrderResult toOrderResult(Order o) {
        return new OrderResult(o);
    }

    public OrderResult toOrderResult(OrderJPAEntity o) {
        return toOrderResult(toOrder(o));
    }
}
