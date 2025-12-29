package sys.bac.adapters.in.api.adapter.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.OrderResult;
import sys.bac.application.port.in.DeleteOrderUseCase;
import sys.bac.application.port.in.GetOrderByIdUseCase;
import sys.bac.application.port.in.GetOrdersUseCase;
import sys.bac.application.port.in.PostOrderUseCase;
import sys.bac.application.port.in.PutOrderUseCase;

@ApplicationScoped
public class OrderServiceAdapter {

    @Inject
    GetOrderByIdUseCase getOrderByIdUC;

    @Inject
    GetOrdersUseCase getOrdersUC;

    @Inject
    PostOrderUseCase postOrderUC;

    @Inject
    PutOrderUseCase putOrderUC;

    @Inject
    DeleteOrderUseCase deleteOrderUC;

    private final Mapper mapper = new Mapper();

    public OrderDTO getOrderById(long id) {
        OrderResult res = getOrderByIdUC.loadOrderById(id);
        if (res.hasError()) {
            
            throw new IllegalArgumentException(res.getMessage());
        }
        return mapper.toDTO(res.getResult());
    }

    public List<OrderDTO> getOrders(String query) {
    
        return getOrdersUC.findOrders();
    }

    public OrderDTO createOrder(OrderDTO dto) {
        NoContentResult res = postOrderUC.createOrder(dto);
        if (res.hasError()) {
            throw new IllegalArgumentException(res.getMessage());
        }
        dto.setId(res.getId());
        return dto;
    }

    public OrderDTO updateOrder(long id, OrderDTO dto) {
        putOrderUC.updateOrder(id, dto);
        dto.setId(id);
        return dto;
    }

    public NoContentResult deleteOrder(long id) {
        return deleteOrderUC.deleteOrder(id);
    }
}
