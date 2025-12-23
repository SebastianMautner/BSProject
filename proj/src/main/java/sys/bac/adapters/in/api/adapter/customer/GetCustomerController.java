package sys.bac.adapters.in.api.adapter.customer;

import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sys.bac.adapters.in.api.adapter.customer.CustomerServiceAdapter;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.port.in.GetCustomerByIdUseCase;

@Path("/customers")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class GetCustomerController {

    private GetCustomerByIdUseCase gCBIUC;

    private UriInfo uriInfo;

    @GET
    public Response getCustomerById(@Positive @PathParam("customerId")long cId) {
        final CustomerDTO customer = gCBIUC.loadCustomerById(new LongId(cId)).getCustomerDTO();
        linkSelf(customer);
    }

    private void linkSelf(CustomerDTO cDTO) {
    }
}
