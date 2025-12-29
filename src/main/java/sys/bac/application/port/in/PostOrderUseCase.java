package sys.bac.application.port.in;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.results.NoContentResult;

public interface PostOrderUseCase {
    NoContentResult createOrder(OrderDTO order);
}
