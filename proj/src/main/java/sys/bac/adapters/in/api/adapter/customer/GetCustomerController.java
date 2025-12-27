package sys.bac.adapters.in.api.adapter.customer;

import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.port.in.GetCustomerByIdUseCase;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON) //maybe XML as well later
public class GetCustomerController {

    private final GetCustomerByIdUseCase gCBIUC;

    public GetCustomerController(GetCustomerByIdUseCase gCBIUC) {
        this.gCBIUC = gCBIUC;
    }

    @GET
    @Path("{cId}")
    public CustomerResult getCustomerById(@Positive @PathParam("customerId")long cId) {
        CustomerDTO customer;
            try {
                customer = gCBIUC.loadCustomerById(cId);
            }
            catch ( NotFoundException e) {
                throw new NotFoundException(Response.status(Response.Status.NOT_FOUND).entity(Response.Status.NOT_FOUND.getStatusCode() + "No customer with Id " + cId).build());
            }
            return new CustomerResult(customer);
    }
}
