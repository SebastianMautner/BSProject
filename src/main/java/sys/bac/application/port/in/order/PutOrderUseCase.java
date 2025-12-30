package sys.bac.application.port.in.order;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.results.NoContentResult;


public interface PutOrderUseCase {
    public NoContentResult updateOrder(long id, OrderDTO order);
}

