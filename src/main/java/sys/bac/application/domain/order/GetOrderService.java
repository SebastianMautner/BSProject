package sys.bac.application.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.OrderResult;
import sys.bac.application.port.in.GetOrderByIdUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class GetOrderService implements GetOrderByIdUseCase {

    @Inject
    private OrderRepository orderRepo;

    public OrderResult loadOrderById(long oId) {
        return orderRepo.getOrderById(new LongId(oId));
    }
}

