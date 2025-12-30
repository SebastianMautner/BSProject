package sys.bac.application.port.in.order;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.order.OrderResult;

public interface GetOrderByIdUseCase {
    public OrderResult loadOrderById(LongId oId);
}

