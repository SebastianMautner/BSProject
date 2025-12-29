package sys.bac.adapters.out.persistence.order;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.order.Order;

public class Mapper {

    public OrderJPAEntity toJPA(Order order) {
        OrderJPAEntity e = new OrderJPAEntity();
        e.setCustomerId(order.getCustomerId());
        e.setSerialNumber(order.getSerialNumber());
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
                e.getSerialNumber(),
                e.getIssueNotes(),
                e.getReceivedAt(),
                e.getCompletion(),
                e.getCostEstimation(),
                e.getFinalCost(),
                e.getStatus()
        );
    }
}
