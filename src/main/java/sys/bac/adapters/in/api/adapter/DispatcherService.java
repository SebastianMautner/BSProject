package sys.bac.adapters.in.api.adapter;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sys.bac.adapters.in.api.models.Link;

@Path("")
public class DispatcherService {

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getPaths() {
        return Response.ok()
        .header("Link", Link.customers.getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString()))
        .build();
    }

    @DELETE
    public void ErrorDelete() {
        throw new NotAllowedException("Only Get is allowed.");
    }
    
    @PUT
    public void ErrorUpdate() {
        throw new NotAllowedException("Only Get is allowed.");
    }
    
    @POST
    @Path("{id}")
    public void ErrorPost() {
        throw new NotAllowedException("Only Get is allowed.");
    }

    @GET
    @Path("id")
    public void ErrorGet() {
        throw new NotAllowedException("Only Get is allowed.");
    }
}
