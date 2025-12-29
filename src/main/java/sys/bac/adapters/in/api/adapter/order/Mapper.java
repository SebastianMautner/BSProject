package sys.bac.adapters.in.api.adapter.order;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.models.order.Order;

public class Mapper {

    public Order toOrder(OrderDTO oDTO) {
        return new Order(oDTO.getId(), oDTO.getNote());
    }

    public OrderDTO toDTO(Order o) {
        return new OrderDTO(o.getOrderId(), o.getNote());
    }
}

