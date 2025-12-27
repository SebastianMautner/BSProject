package sys.bac.adapters.in.api.adapter.customer;

import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.port.in.PutCustomerUseCase;

@Path("/customers")
public class PutCustomerController {

    private final PutCustomerUseCase pCUC;

    public PutCustomerController(PutCustomerUseCase pCUC) {
        this.pCUC = pCUC;
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON) //XML
    public void updateCustomer(@Positive @PathParam("id") long id, CustomerDTO customer) {
        pCUC.updateCustomer(customer);
    }
}
