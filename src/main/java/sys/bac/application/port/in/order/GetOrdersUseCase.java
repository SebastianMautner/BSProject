package sys.bac.application.port.in.order;

import sys.bac.application.domain.results.order.OrdersResult;

public interface GetOrdersUseCase {
    public OrdersResult findOrders(String query);
}

