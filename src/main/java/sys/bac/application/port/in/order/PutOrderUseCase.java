package sys.bac.application.port.in.order;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;


public interface PutOrderUseCase {
    public NoContentResult updateOrder(LongId id, OrderDTO order);
}

