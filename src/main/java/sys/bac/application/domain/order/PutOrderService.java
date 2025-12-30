package sys.bac.application.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.adapters.in.api.adapter.order.Mapper;
import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.order.OrderResult;
import sys.bac.application.port.in.order.PutOrderUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class PutOrderService implements PutOrderUseCase {

    private Mapper mapper = new Mapper();

    @Inject
    private OrderRepository orderRepo;

    public NoContentResult updateOrder(LongId id, OrderDTO order) {
        OrderResult exists = orderRepo.getOrderById(id);
        NoContentResult result = new NoContentResult();

        if (exists.isEmpty()) {
            result.setError(404, "NotFound");
        } else if (exists.hasError()){
            result.setError(500, exists.getMessage());
        } else {
            result = orderRepo.update(id, mapper.toOrder(order));
        }
        return result;
    }
}
