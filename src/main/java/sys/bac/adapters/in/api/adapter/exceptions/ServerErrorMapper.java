package sys.bac.adapters.in.api.adapter.exceptions;

import jakarta.ws.rs.InternalServerErrorException;
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
public class ServerErrorMapper implements ExceptionMapper<InternalServerErrorException>{

    @Context
    private ResourceInfo resource;

    @Context private UriInfo uriInfo;


    public Response toResponse(InternalServerErrorException ex) {
        Class<?> resourceClass = resource.getResourceClass();
        if (resourceClass == CustomerWebController.class) {
            return Response.serverError().entity(ex.getMessage()).header("Link", Link.customers.getHeaderLink(uriInfo.getBaseUri().toString())).build();
        } else if (resourceClass == OrderWebController.class) {
            return Response.serverError().header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString())).build();
        }
        else if(resourceClass == DeviceWebController.class) {
            return Response.serverError().header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString())).build();
        }
        else if (resourceClass == DispatcherService.class) {
            return Response.serverError().header("Link", new Link("", "tryDispatcherAgain", "application/json").getHeaderLink(uriInfo.getBaseUri().toString())).build();
        }
        else {
            return Response.serverError().header("Class", resourceClass.getName()).header("Link", new Link("", "tryDispatcher", "application/json").getHeaderLink(uriInfo.getBaseUri().toString())).build();
        }
        
    }
}
