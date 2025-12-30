package sys.bac.adapters.in.api.adapter.exceptions;

import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import sys.bac.adapters.in.api.adapter.DispatcherService;
import sys.bac.adapters.in.api.adapter.customer.CustomerWebController;
import sys.bac.adapters.in.api.adapter.device.DeviceWebController;
import sys.bac.adapters.in.api.adapter.order.OrderWebController;
import sys.bac.adapters.in.api.models.Link;

@Provider
public class MethodNotAllowedMapper implements ExceptionMapper<NotAllowedException>{
    @Context
    private ResourceInfo resource;
    
    public Response toResponse(NotAllowedException ex) {
        Class<?> resourceClass = resource.getResourceClass();
        if (resourceClass == CustomerWebController.class) {
            return Response.status(405).header("Link", Link.customers.getHeaderLink()).build();
        }
        else if (resourceClass == OrderWebController.class) {
            return Response.status(405).header("Link", Link.orders.getHeaderLink()).build();
        }
        else if (resourceClass == DeviceWebController.class) {
            return Response.status(405).header("Link", Link.devices.getHeaderLink()).build();
        }
        else if (resourceClass == DispatcherService.class) {
            return Response.status(405)
            .header("Link", new Link("", "getDispatcherService", "application/json").getHeaderLink()).build();
        }
        else {
            return Response.status(405)
            .header("How", "YouFailedHypermedia")
            .header("Link", new Link("http://localhost/", "getDispatcherService", "application/json").getHeaderLink()).build();
        }
        
    }
}
