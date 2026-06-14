package sys.bac.adapters.in.api.adapter.exceptions;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import sys.bac.adapters.in.api.adapter.DispatcherService;
import sys.bac.adapters.in.api.adapter.customer.CustomerWebController;
import sys.bac.adapters.in.api.adapter.device.DeviceWebController;
import sys.bac.adapters.in.api.adapter.order.OrderWebController;
import sys.bac.adapters.in.api.models.Link;

@Provider
public class BadRequestMapper implements ExceptionMapper<BadRequestException>{
    
    @Context
    private ResourceInfo resource;

    @Context private UriInfo uriInfo;
    
    public Response toResponse(BadRequestException ex) {
        Class<?> resourceClass = resource.getResourceClass();
        if (resourceClass == CustomerWebController.class) {
            return dashboard(Response.status(400)).entity(ex.getMessage()).header("Link", Link.customers.getHeaderLink(uriInfo.getBaseUri().toString())).build();
        }
        else if (resourceClass == OrderWebController.class) {
            return dashboard(Response.status(400)).entity(ex.getMessage()).header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString())).build();
        }
        else if (resourceClass == DeviceWebController.class) {
            return dashboard(Response.status(400)).entity(ex.getMessage()).header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString())).build();
        }
        else if (resourceClass == DispatcherService.class) {
            return dashboard(Response.status(400))
            .entity(ex.getMessage())
            .header("Link", new Link("", "getDispatcherService", "application/json").getHeaderLink(uriInfo.getBaseUri().toString())).build();
        }
        else {
            return dashboard(Response.status(400))
            .entity(ex.getMessage())
            .header("Link", new Link("", "getDispatcherService", "application/json").getHeaderLink(uriInfo.getBaseUri().toString())).build();
        }
    }

    private Response.ResponseBuilder dashboard(
        Response.ResponseBuilder builder) {
            
            return builder
            .header("Link", Link.customers.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link",
            new Link("/", "dashboard", "application/json")
            .getHeaderLink(uriInfo.getBaseUri().toString()));
    }
}
