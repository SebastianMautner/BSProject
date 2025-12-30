package sys.bac.application.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sys.bac.application.domain.results.order.OrdersResult;
import sys.bac.application.port.in.order.GetOrdersUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class GetOrdersService implements GetOrdersUseCase {

    @Inject
    private OrderRepository orderRepo;

    public OrdersResult findOrders(String query) {
        return orderRepo.getAllOrders(query);
    }
}
