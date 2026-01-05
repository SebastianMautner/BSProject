package sys.bac.application.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.port.in.order.GetOrderByIdUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class GetOrderService implements GetOrderByIdUseCase {
    
    @Inject
    private OrderRepository orderRepo;
    
    public OrderResult loadOrderById(LongId oId) {
        OrderResult result = orderRepo.getOrderById(oId);
        if (result.isEmpty()) {
            result.setError(404, "NotFound");
        }
        return result;
    }
}

