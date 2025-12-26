package sys.bac.application.port.in;

import sys.bac.application.domain.models.LongId;

public interface DeleteCustomerUseCase {
    void deleteCustomer(LongId id);
}
