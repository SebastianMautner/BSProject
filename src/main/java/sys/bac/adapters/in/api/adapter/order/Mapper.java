package sys.bac.adapters.in.api.adapter.order;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.order.Order;

public class Mapper {

    public Order toOrder(OrderDTO dto) {
        return new Order(
                new LongId(dto.getId()),
                dto.getCustomerId(),
                dto.getSerialNumber(),
                dto.getIssueNotes(),
                dto.getReceivedAt(),
                dto.getCompletion(),
                dto.getCostEstimation(),
                dto.getFinalCost(),
                dto.getStatus()
        );
    }

    public OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomerId());
        dto.setSerialNumber(order.getSerialNumber());
        dto.setIssueNotes(order.getIssueNotes());
        dto.setReceivedAt(order.getReceivedAt());
        dto.setCompletion(order.getCompletion());
        dto.setCostEstimation(order.getCostEstimation());
        dto.setFinalCost(order.getFinalCost());
        dto.setStatus(order.getStatus());
        return dto;
    }
}

