package sys.bac.application.port.in.order;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;

public interface DeleteOrderUseCase {
    public NoContentResult deleteOrder(LongId id);
}


