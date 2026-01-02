package sys.bac.adapters.in.api.adapter.order;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.inject.Inject;

import java.net.URI;
import java.util.stream.Collectors;
import java.util.Objects;

import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.adapters.in.api.models.OrdersApiResult;
import sys.bac.adapters.in.api.models.Link;


@Path("orders")
public class OrderWebController {
        
        @Inject
        private OrderServiceAdapter oSA;

                
        @Context
        Request request;


        private CacheControl defaultGetCacheControl() {
                CacheControl cc = new CacheControl();
                cc.setPrivate(true);
                cc.setMaxAge(30);
                cc.setMustRevalidate(true); 
                return cc;
        }


        private CacheControl noStore() {
                CacheControl cc = new CacheControl();
                cc.setNoStore(true);
                cc.setNoCache(true);
                return cc;
        }

        private EntityTag etagOf(Object... parts) {
                String value = Integer.toHexString(Objects.hash(parts));
                return new EntityTag(value, true);
        }
        
        @Context
        private UriInfo uriInfo;
        private URI uri = uriInfo.getAbsolutePath();
        
        @GET
        @Path("{orderId}")
        @Produces(MediaType.APPLICATION_JSON)
        public Response getOrderById(@Positive @PathParam("orderId") long id) { 
                OrderDTO order = oSA.getOrderById(id);
                order = addSelfLink(order, "getOrderWithId" + order.getId());

                EntityTag etag = etagOf(order);

                Response.ResponseBuilder precond = request.evaluatePreconditions(etag);
                if (precond != null) {
                return precond
                        .cacheControl(defaultGetCacheControl())
                        .tag(etag)
                        .header("Link", Link.devices.getHeaderLink())
                        .build();
                }
                return Response.ok(order)
                        .cacheControl(defaultGetCacheControl())
                        .tag(etag)
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
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size, "prev", "application/json").getHeaderLink(uri))
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink(uri));
                                
                        } else if(orders.next()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink(uri));
                                
                        } else if(orders.prev()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset - size) + "&size=" + size, "prev", "application/json").getHeaderLink(uri));
                        }
                }
                else {
                        if (orders.next() && orders.prev()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink(uri))
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink(uri));
                                
                        } else if(orders.next()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink(uri));
                                
                        } else if(orders.prev()) {
                                builder = builder
                                .header("Link", new Link(Link.orders.getHref() + "?offset=" + (offset - size) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink(uri));
                        }
                        builder.header("Link", new Link(Link.orders.getHref(), "clearQuery", "application/json").getHeaderLink(uri));
                }
                return builder
                .header("Link", new Link(Link.orders.getHref() + "?query={query}", "getNewOrderQuery", "application/json").getHeaderLink(uri))
                .header("Link", Link.devices.getHeaderLink(uri))
                .header("Link", Link.customers.getHeaderLink(uri))
                .header("Link", new Link(Link.orders.getHref(), "createOrder", "application/json").getHeaderLink(uri))
                .build();
        }
        
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public Response postOrder(@Valid OrderDTO order) {
                OrderDTO result = oSA.createOrder(order);
                
                
                return Response.status(Response.Status.CREATED)
                .cacheControl(noStore())
                .header("Location", new Link(Link.orders.getHref() + "/" + result.getId(), "getOrder", "application/json").getHeaderLink())
                .build();
        }
        
        @PUT
        @Path("{id}")
        @Consumes(MediaType.APPLICATION_JSON)
        public Response updateOrder(@Positive @PathParam("id") long id, @Valid OrderDTO order) {
                oSA.updateOrder(id, order);
                return Response.noContent()
                .cacheControl(noStore())
                .header("Link", new Link(Link.orders.getHref() + "/" + id, "getOrder", "application/json").getHeaderLink()).build();
        }
        
        @DELETE
        @Path("{id}")
        public Response deleteOrder(@Positive @PathParam("id") long id) {
                oSA.deleteOrder(id);
                return Response.noContent()
                .cacheControl(noStore())
                .header("Link", Link.orders.getHeaderLink())
                .build();
        }
        
        private OrderDTO addSelfLink(OrderDTO order, String rel) {
               order.setSelf(new Link(uriInfo.getAbsolutePath().toString() + "/" + order.getId(), rel, "application/json"));
                return order;
        }
        
        @DELETE
        public void ErrorDelete() {
                throw new NotAllowedException("No DELETE for path orders/");
        }
        
        @PUT
        public void ErrorUpdate() {
                throw new NotAllowedException("No PUT for path orders/");
        }
        
        @POST
        @Path("{id}")
        public void ErrorPost() {
                throw new NotAllowedException("No POST for path orders/id");
        }
}