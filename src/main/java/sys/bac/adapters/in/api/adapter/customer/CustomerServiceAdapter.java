package sys.bac.adapters.in.api.adapter.customer;

import jakarta.inject.Inject;
import sys.bac.application.port.in.DeleteCustomerUseCase;
import sys.bac.application.port.in.GetCustomerByIdUseCase;
import sys.bac.application.port.in.GetCustomersUseCase;
import sys.bac.application.port.in.PostCustomerUseCase;
import sys.bac.application.port.in.PutCustomerUseCase;

public class CustomerServiceAdapter {

    @Inject
    private GetCustomerByIdUseCase gCBIUC;

    @Inject
    private GetCustomersUseCase gCUC;

    @Inject
    private PostCustomerUseCase poCUC;

    @Inject
    private PutCustomerUseCase puCUC;

    @Inject
    private DeleteCustomerUseCase dCUC;

    
}
