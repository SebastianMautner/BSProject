package sys.bac.application.port.in.order;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.results.order.OrderResult;

public interface PostOrderUseCase {
    public OrderResult createOrder(OrderDTO order);
}
