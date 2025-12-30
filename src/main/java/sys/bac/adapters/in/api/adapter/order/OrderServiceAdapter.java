package sys.bac.adapters.in.api.adapter.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;

import java.util.stream.Collectors;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.adapters.in.api.models.OrdersApiResult;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.domain.results.order.OrdersResult;
import sys.bac.application.port.in.order.DeleteOrderUseCase;
import sys.bac.application.port.in.order.GetOrderByIdUseCase;
import sys.bac.application.port.in.order.GetOrdersUseCase;
import sys.bac.application.port.in.order.PostOrderUseCase;
import sys.bac.application.port.in.order.PutOrderUseCase;

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
        LongId oId = new LongId(id);
        OrderResult res = getOrderByIdUC.loadOrderById(oId);
        if (res.isEmpty()) {
            throw new NotFoundException();
        } else if (res.hasError()) {
            throw new IllegalArgumentException(res.getMessage());
        }
        else {
            return mapper.toDTO(res.getResult());
        }
    }
    
    public OrdersApiResult getOrders(String query, int offset, int size) {
        OrdersResult orders = getOrdersUC.findOrders(query, offset, size);
        if(orders.hasError()) {
            throw new InternalServerErrorException(orders.getMessage());
        }
        OrdersApiResult result= new OrdersApiResult(orders.getResult().getResult()
        .stream()
        .map(mapper::toDTO)
        .collect(Collectors.toList()), orders.getResult().getTotalElements() > offset + size, offset != 0);
        return result; 
    }
    
    public OrderDTO createOrder(OrderDTO dto) {
        OrderResult res = postOrderUC.createOrder(dto);
        if (res.hasError()) {
            throw new IllegalArgumentException(res.getMessage());
        }
        return mapper.toDTO(res.getResult());
    }
    
    public void updateOrder(long id, OrderDTO dto) {
        LongId oId = new LongId(id);
        NoContentResult result = putOrderUC.updateOrder(oId, dto);
        if (result.getErrorCode() == 404) {
            throw new NotFoundException();
        } else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
    }
    
    public void deleteOrder(long id) {
        LongId oId = new LongId(id);
        NoContentResult result= deleteOrderUC.deleteOrder(oId);
        if (result.getErrorCode() == 404) {
            throw new NotFoundException();
        } 
        else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
    }
}
