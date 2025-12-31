package sys.bac.application.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.order.JpaOrdersResult;
import sys.bac.application.domain.results.order.OrdersResult;
import sys.bac.application.port.in.order.GetOrdersUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class GetOrdersService implements GetOrdersUseCase {

    @Inject
    private OrderRepository orderRepo;

    public OrdersResult findOrders(String query, int offset, int size) {
        JpaOrdersResult jpaResult = orderRepo.getAllOrders(query, offset, size);
        LongResult totalResult = orderRepo.count();
        
        OrdersResult result = new OrdersResult(new Page<>(jpaResult.getResult(), offset, size, totalResult.getResult()));
        if (jpaResult.hasError() || totalResult.hasError()) {
            result.setError(500, jpaResult.getMessage() + "\n" + totalResult.getMessage());
        }
        return result;
    }
}
