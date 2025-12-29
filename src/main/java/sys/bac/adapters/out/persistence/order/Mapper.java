package sys.bac.adapters.out.persistence.order;

import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.OrderResult;

public class Mapper {

    public OrderJPAEntity toJPA(Order order) {
        // ID wird von JPA generiert – wir übernehmen hier nur die Daten
        return new OrderJPAEntity(order.getNote());
    }

    public Order toOrder(OrderJPAEntity entity) {
        if (entity == null) return null;
        return new Order(new LongId(entity.getId()), entity.getNote());
    }

    public OrderResult toOrderResult(OrderJPAEntity entity) {
        return new OrderResult(toOrder(entity));
    }
}
