package sys.bac.application.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.port.in.order.DeleteOrderUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class DeleteOrderService implements DeleteOrderUseCase {

    @Inject
    private OrderRepository orderRepo;

    public NoContentResult deleteOrder(LongId id) {
        OrderResult exists = orderRepo.getOrderById(id);
        NoContentResult result = new NoContentResult();

        if (exists.isEmpty()) {
            result.setError(404, "NotFound");
        } else if (exists.hasError()){
            result.setError(500, exists.getMessage());
        } else {
            result = orderRepo.delete(id);
        }
        return result;
    }
}

