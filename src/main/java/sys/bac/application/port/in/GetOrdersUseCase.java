package sys.bac.application.port.in;

import java.util.List;
import sys.bac.adapters.in.api.models.OrderDTO;

public interface GetOrdersUseCase {
    List<OrderDTO> findOrders();
}

