package sys.bac.adapters.in.api.adapter.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
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

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderServiceAdapter {

    private static final Logger LOG = Logger.getLogger(OrderServiceAdapter.class);
    
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
    
    @CacheResult(cacheName = "order-by-id")
    public OrderDTO getOrderById(@CacheKey long id) {
        LOG.infof("CACHE-TEST: getOrderById EXECUTED for id=%d", id);
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

    @CacheResult(cacheName = "orders-list")
    public OrdersApiResult getOrders(String query, int offset, int size) {
        LOG.infof("CACHE-TEST: getOrders EXECUTED for query=%s, offset=%d, size=%d", query, offset, size);
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

    @CacheInvalidateAll(cacheName = "orders-list")
    public OrderDTO createOrder(OrderDTO dto) {
        LOG.infof("CREATE order → cache invalidated");
        if (dto == null) {
            throw new BadRequestException("Body should contain the new Object");
        }
        OrderResult res = postOrderUC.createOrder(dto);
        if (res.hasError()) {
            throw new IllegalArgumentException(res.getMessage());
        }
        return mapper.toDTO(res.getResult());
    }

    @CacheInvalidate(cacheName = "order-by-id")
    @CacheInvalidateAll(cacheName = "orders-list")
    public void updateOrder(@CacheKey long id, OrderDTO dto) {  
        LOG.infof("UPDATE order id=%d → cache invalidated", id);
        if (dto == null) {
            throw new BadRequestException("Body should contain the new Object");
        }
        LongId oId = new LongId(id);
        NoContentResult result = putOrderUC.updateOrder(oId, dto);
        if (result.getErrorCode() == 404) {
            throw new NotFoundException();
        } else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
    }

    @CacheInvalidate(cacheName = "order-by-id")
    @CacheInvalidateAll(cacheName = "orders-list")
    public void deleteOrder(@CacheKey long id) {
        LOG.infof("DELETE order id=%d → cache invalidated", id);
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