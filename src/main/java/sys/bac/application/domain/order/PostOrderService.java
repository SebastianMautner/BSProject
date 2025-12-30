package sys.bac.application.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.adapters.in.api.adapter.order.Mapper;
import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.port.in.order.PostOrderUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class PostOrderService implements PostOrderUseCase {

    private Mapper mapper = new Mapper();

    @Inject
    private OrderRepository orderRepo;

    public NoContentResult createOrder(OrderDTO order) {
        return orderRepo.create(mapper.toOrder(order));
    }
}

