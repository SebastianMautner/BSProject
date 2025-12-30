package sys.bac.application.port.in;

import sys.bac.adapters.in.api.models.OrderDTO;

public interface PutOrderUseCase {
    void updateOrder(long id, OrderDTO order);
}

