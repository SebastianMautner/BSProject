package sys.bac.adapters.in.api.adapter.customer;


import org.jboss.resteasy.spi.InternalServerErrorException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.port.in.GetCustomerByIdUseCase;


@ApplicationScoped
public class CustomerServiceAdapter {
    
    private GetCustomerByIdUseCase gCBIUC;

    public CustomerResult getCustomerById(long cId) {
        final LongId id= new LongId(cId);
        final var result = this.gCBIUC.loadCustomerById(id);

        if(result.isEmpty()) {
            throw new NotFoundException();
        }
        else if(result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
        else {
            return new CustomerResult(); // TODO
        }
    }
}
