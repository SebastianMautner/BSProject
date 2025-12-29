package sys.bac.application.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.port.in.DeleteOrderUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class DeleteOrderService implements DeleteOrderUseCase {

    @Inject
    private OrderRepository orderRepo;

    public void deleteOrder(long id) {
        orderRepo.delete(new LongId(id));
    }
}
