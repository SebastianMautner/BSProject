package sys.bac.adapters.in.api.adapter.customer;

import java.util.List;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.port.in.GetCustomersUseCase;

@Path("/Customers")
@Produces(MediaType.APPLICATION_JSON) //maybe XML as well later
public class GetCustomersController {
    private final GetCustomersUseCase gCUC;

    public GetCustomersController(GetCustomersUseCase gCUC) {
        this.gCUC = gCUC;
    }

    @GET
    public CustomersResult getCustomers(@DefaultValue("") @QueryParam("query") String query) {
        List<CustomerDTO> result;
        // The Method-Path for this method is the following:
        // Controller->Use Case -(implemented by)> Service -> Repo -(implemented by)> JpaAdapter
        try {
            result = gCUC.findCustomers(query);
        }
        catch(IllegalArgumentException e) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(Response.Status.BAD_REQUEST.getStatusCode() + "Invalid 'query'").build()); // amazing line btw (only took like 5 minutes)
        }

        return new CustomersResult(result);
    }
}
