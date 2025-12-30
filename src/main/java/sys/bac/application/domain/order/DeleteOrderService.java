package sys.bac.application.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.port.in.order.DeleteOrderUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class DeleteOrderService implements DeleteOrderUseCase {

    @Inject
    private OrderRepository orderRepo;

    @Override
    public NoContentResult deleteOrder(long id) {
        return orderRepo.delete(new LongId(id));
    }
}

