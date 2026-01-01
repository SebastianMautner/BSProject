package sys.bac.adapters.in.api.adapter.order;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import jakarta.inject.Inject;

import java.util.stream.Collectors;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.adapters.in.api.models.OrdersApiResult;
import sys.bac.adapters.in.api.models.Link;


@Path("orders")
public class OrderWebController {
        
        @Inject
        private OrderServiceAdapter oSA;
        
        @GET
        @Path("{orderId}")
        @Produces(MediaType.APPLICATION_JSON)
        public Response getOrderById(@Positive @PathParam("orderId") long id) { 
                OrderDTO order = oSA.getOrderById(id);
                order = addSelfLink(order, "getOrderWithId" + order.getId());
                return Response.ok(order)
                .header("Link", Link.orders.getHeaderLink())
                .header("Link", new Link(Link.orders.getHref() + "/" + id, "updateOrder", "application/json").getHeaderLink())
                .header("Link", new Link(Link.orders.getHref() + "/" + id, "deleteOrder", "application/json").getHeaderLink())
                .build();
        }
        
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response getOrders(@DefaultValue("") @QueryParam("query") String query,
        @PositiveOrZero @DefaultValue("0") @QueryParam("offset") int offset,
        @PositiveOrZero @DefaultValue("2") @QueryParam("size") int size) {
                size = Math.min(100, size);
                
                OrdersApiResult orders = oSA.getOrders(query, offset, size);
                orders.setResult(orders.getResult().stream().map(o -> addSelfLink(o, "getOrderWithId" + o.getId())).collect(Collectors.toList()));
                Response.ResponseBuilder builder = Response.ok(orders.getResult());
                
                if(query.isBlank()) {
                        if (orders.next() && orders.prev()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size, "prev", "application/json").getHeaderLink())
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink());
                                
                        } else if(orders.next()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink());
                                
                        } else if(orders.prev()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset - size) + "&size=" + size, "prev", "application/json").getHeaderLink());
                        }
                }
                else {
                        if (orders.next() && orders.prev()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink())
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink());
                                
                        } else if(orders.next()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink());
                                
                        } else if(orders.prev()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset - size) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink());
                        }
                        builder.header("Link", new Link(Link.orders.getHref(), "clearQuery", "application/json").getHeaderLink());
                }
                return builder
                .header("Link", new Link(Link.orders.getHref() + "?query={query}", "getNewOrderQuery", "application/json").getHeaderLink())
                .header("Link", Link.devices.getHeaderLink())
                .header("Link", Link.customers.getHeaderLink())
                .header("Link", new Link(Link.orders.getHref(), "createOrder", "application/json").getHeaderLink())
                .build();
        }
        
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public Response postOrder(@Valid OrderDTO order) {
                OrderDTO result = oSA.createOrder(order);
                
                
                return Response.status(Response.Status.CREATED)
                .header("Location", new Link(Link.orders.getHref() + "/" + result.getId(), "getOrder", "application/json").getHeaderLink())
                .build();
        }
        
        @PUT
        @Path("{id}")
        @Consumes(MediaType.APPLICATION_JSON)
        public Response updateOrder(@Positive @PathParam("id") long id, @Valid OrderDTO order) {
                oSA.updateOrder(id, order);
                return Response.noContent().header("Link", new Link(Link.orders.getHref() + "/" + id, "getOrder", "application/json").getHeaderLink()).build();
        }
        
        @DELETE
        @Path("{id}")
        public Response deleteOrder(@Positive @PathParam("id") long id) {
                oSA.deleteOrder(id);
                return Response.noContent()
                .header("Link", Link.orders.getHeaderLink())
                .build();
        }
        
        private OrderDTO addSelfLink(OrderDTO order, String rel) {
                order.setSelf(new Link("http://localhost:8080/" + Link.orders.getHref() + "/" + order.getId(),rel,"application/json"));
                return order;
        }
        
        @DELETE
        public void ErrorDelete() {
                throw new BadRequestException();
        }
        
        @PUT
        public void ErrorUpdate() {
                throw new BadRequestException();
        }
        
        @POST
        @Path("{id}")
        public void ErrorPost() {
                throw new BadRequestException();
        }
}