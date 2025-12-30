package sys.bac.application.domain.order;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.adapters.in.api.adapter.order.Mapper;
import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.application.port.in.order.GetOrdersUseCase;
import sys.bac.application.port.out.OrderRepository;

@ApplicationScoped
public class GetOrdersService implements GetOrdersUseCase {

    @Inject
    private OrderRepository orderRepo;

    private Mapper mapper = new Mapper();

    public GetOrdersService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
        mapper = new Mapper();
    }

    public List<OrderDTO> findOrders() {
        return orderRepo.getAllOrders()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
}
