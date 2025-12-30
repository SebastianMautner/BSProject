package sys.bac.adapters.in.api.adapter.exceptions;

import jakarta.ws.rs.InternalServerErrorException;
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
public class ServerErrorMapper implements ExceptionMapper<InternalServerErrorException>{

    @Context
    private ResourceInfo resource;

    public Response toResponse(InternalServerErrorException ex) {
        Class<?> resourceClass = resource.getResourceClass();
        if (resourceClass == CustomerWebController.class) {
            return Response.serverError().header("Link", Link.customers.getHeaderLink()).build();
        } else if (resourceClass == OrderWebController.class) {
            return Response.serverError().header("Link", Link.orders.getHeaderLink()).build();
        }
        else if(resourceClass == DeviceWebController.class) {
            return Response.serverError().header("Link", Link.devices.getHeaderLink()).build();
        }
        else if (resourceClass == DispatcherService.class) {
            return Response.serverError().header("Link", new Link("http://localhost:8080/", "tryDispatcherAgain", "application/json")).build();
        }
        else {
            return Response.serverError().header("Class", resourceClass.getName()).build();
        }
        
    }
}
