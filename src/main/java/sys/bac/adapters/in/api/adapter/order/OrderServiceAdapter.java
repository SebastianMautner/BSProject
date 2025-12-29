package sys.bac.adapters.in.api.adapter.order;

import jakarta.inject.Inject;

import sys.bac.application.port.in.DeleteOrderUseCase;
import sys.bac.application.port.in.GetOrderByIdUseCase;
import sys.bac.application.port.in.GetOrdersUseCase;
import sys.bac.application.port.in.PostOrderUseCase;
import sys.bac.application.port.in.PutOrderUseCase;

public class OrderServiceAdapter {

    @Inject
    private GetOrderByIdUseCase gOBIUC;

    @Inject
    private GetOrdersUseCase gOUC;

    @Inject
    private PostOrderUseCase poOUC;

    @Inject
    private PutOrderUseCase puOUC;

    @Inject
    private DeleteOrderUseCase dOUC;
}
