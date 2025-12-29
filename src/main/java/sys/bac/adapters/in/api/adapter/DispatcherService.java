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
        Link customers = new Link("customers", "getAllCustomers", "application/json");
        Link orders = new Link("orders", "getAllOrders", "applicaiton/json");
        Link devices = new Link("devices", "getAllDevices", "application/json");
        Response response = builder.header("Link", customers.getHeaderLink()).header("Link", orders.getHeaderLink()).header("Link", devices.getHeaderLink()).build();
        return response;
    }
}
