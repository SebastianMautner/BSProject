package sys.bac.application.port.in;

import sys.bac.application.domain.results.NoContentResult;

public interface DeleteOrderUseCase {
    NoContentResult deleteOrder(long id);
}


