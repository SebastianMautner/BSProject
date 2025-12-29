package sys.bac.adapters.in.api.adapter;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sys.bac.adapters.in.api.models.Link;

@Path("")
public class DispatcherService {

    @Context
    UriInfo uriInfo;
    
    @GET
    public Response getPaths() {
        final Response.ResponseBuilder builder = Response.ok();
        Link link = new Link("customers", "getAllCustomers", "application/json");
        Response response = builder.header("Link", link.getHeaderLink()).build();
        return response;
    }
}
