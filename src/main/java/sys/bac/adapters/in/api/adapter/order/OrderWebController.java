package sys.bac.adapters.in.api.adapter.order;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import jakarta.inject.Inject;

import java.util.List;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.adapters.in.api.models.Link;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.port.in.PostOrderUseCase;
import sys.bac.application.port.in.GetOrdersUseCase;
import sys.bac.application.port.in.GetOrderByIdUseCase;
import sys.bac.application.port.in.PutOrderUseCase;
import sys.bac.application.port.in.DeleteOrderUseCase;

@Path("orders")
public class OrderWebController {

    @Inject
    private GetOrderByIdUseCase gOBIUC;

    @Inject
    private GetOrdersUseCase gOUC;

    @Inject
    private PostOrderUseCase poOUC;

    @Inject
    private PutOrderUseCase puOUC;

    @Inject
    private DeleteOrderUseCase dOUC;

    @Context
    UriInfo uriInfo;

    private Mapper mapper = new Mapper();

    @GET
    @Path("{oId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(@PathParam("oId") long oId) {
        OrderDTO order = mapper.toDTO(gOBIUC.loadOrderById(oId).getResult());
        return Response.ok(order)
                .header("Link", new Link("orders", "getAllOrders", "application/json").getHeaderLink())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrders() {
        List<OrderDTO> orders = gOUC.findOrders();
        orders.stream().forEach(o -> o.setSelf(new Link(
                uriInfo.getBaseUriBuilder()
                        .path(OrderWebController.class)
                        .path(OrderWebController.class, "getOrderById")
                        .build(o.getId()).toASCIIString(),
                "getOrderWithId" + o.getId(),
                "application/json"
        )));
        return Response.ok(orders).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postOrder(@Valid OrderDTO order) {
        NoContentResult result = poOUC.createOrder(order);
        if (result.hasError()) {
            return Response.ok(result.getMessage() + "\nTHIS IS NOT 200 OK!").build();
        }
        return Response.status(Response.Status.CREATED)
                .header("Location", uriInfo.getRequestUriBuilder()
                        .path(Long.toString(result.getId()))
                        .build())
                .build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateOrder(@Positive @PathParam("id") long id, OrderDTO order) {
        puOUC.updateOrder(id, order);
    }

    @DELETE
    @Path("{id}")
    public void deleteOrder(@PathParam("id") long id) {
        dOUC.deleteOrder(id);
    }
}
