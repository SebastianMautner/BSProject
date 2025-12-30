package sys.bac.adapters.in.api.adapter.exceptions;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import sys.bac.adapters.in.api.adapter.customer.CustomerWebController;
import sys.bac.adapters.in.api.adapter.order.OrderWebController;
import sys.bac.adapters.in.api.models.Link;

@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException>{
    @Context
    ResourceInfo resource;
    
    public Response toResponse(NotFoundException ex) {
        Class<?> resourceClass = resource.getResourceClass();
        if (resourceClass == CustomerWebController.class) {
            return Response.status(404).header("Link", Link.customers.getHeaderLink()).build();
        }
        else if (resourceClass == OrderWebController.class) {
            return Response.status(404).header("Link", Link.orders.getHeaderLink()).build();
        }
        // else if (resourceClass == DeviceWebController.class) {
        //     return Response.status(404).header("Link", Link.devices.getHeaderLink()).build();
        // }
        return Response.status(404)
        .header("How", "YouFailedHypermedia")
        .header("Link", new Link("http://localhost:8080/", "getDispatcherService", "application/json")).build();
    }
}
