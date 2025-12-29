package sys.bac.application.port.in;

import sys.bac.application.domain.results.OrderResult;

public interface GetOrderByIdUseCase {
    OrderResult loadOrderById(long oId);
}

