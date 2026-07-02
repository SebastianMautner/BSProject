package sys.bac.adapters.in.api.adapter.order;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
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
import jakarta.ws.rs.core.EntityTag;
import jakarta.inject.Inject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
                        .header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString()))
                        .header("Link", new Link(Link.orders.getHref() + "/" + id, "updateOrder", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                        .header("Link", new Link(Link.orders.getHref() + "/" + id, "deleteOrder", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                        .build();
                }
                return dashboard(Response.ok(order))
                .cacheControl(defaultGetCacheControl())
                .tag(etag)
                .header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString()))
                .header("Link", new Link(Link.orders.getHref() + "/" + id, "updateOrder", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                .header("Link", new Link(Link.orders.getHref() + "/" + id, "deleteOrder", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                .build();
        }
        
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response getOrders(@DefaultValue("") @QueryParam("query") String query,
        @DefaultValue("") @QueryParam("status") String status,
        @PositiveOrZero @DefaultValue("0") @QueryParam("page") int page,
        @PositiveOrZero @DefaultValue("0") @QueryParam("offset") int offset,
        @PositiveOrZero @DefaultValue("2") @QueryParam("size") int size) {
                size = Math.min(100, size);
                if (page > 0 && offset == 0) {
                        offset = page * size;
                }
                
                OrdersApiResult orders = status.isBlank() ? oSA.getOrders(query, offset, size) : oSA.getOrders(query, status, offset, size);
                orders.setResult(orders.getResult().stream().map(o -> addSelfLink(o, "getOrderWithId" + o.getId())).collect(Collectors.toList()));
                Response.ResponseBuilder builder = Response.ok(orders.getResult());
                addPaginationHeaders(builder, Link.orders.getHref(), query, status, offset, size, orders.getTotalElements(), orders.next(), orders.prev());

                if(!query.isBlank() || !status.isBlank()) {
                        builder.header("Link", new Link(Link.orders.getHref(), "clearQuery", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
                }

                return dashboard(builder)
                .header("Link", new Link(Link.orders.getHref() + "?query={query}", "getNewOrderQuery", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                .header("Link", new Link(Link.orders.getHref() + "?status={status}", "getNewOrderStatusFilter", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                .header("Link", new Link(Link.orders.getHref(), "createOrder", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                .build();
        }
        
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public Response postOrder(@Valid OrderDTO order) {
                OrderDTO result = oSA.createOrder(order);
                
                
                return Response.status(Response.Status.CREATED)
                .cacheControl(noStore())
                .header("Location", new Link(Link.orders.getHref() + "/" + result.getId(), "getOrder", "application/json").getHeaderHref(uriInfo.getBaseUri().toString()))
                .build();
        }
        
        @PUT
        @Path("{id}")
        @Consumes(MediaType.APPLICATION_JSON)
        public Response updateOrder(@Positive @PathParam("id") long id, @Valid OrderDTO order, @HeaderParam("If-Match") String ifMatch) {
                if (ifMatch == null || ifMatch.isBlank()) {
                        return Response.status(428)
                        .cacheControl(noStore())
                        .entity("Missing If-Match header. Fetch the resource first (GET) and resend PUT with If-Match: <ETag>.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
                }
                OrderDTO current = oSA.getOrderById(id);
                current = addSelfLink(current, "getOrderWithId" + current.getId());
                EntityTag currentEtag = etagOf(current);
                
                Response.ResponseBuilder precond = request.evaluatePreconditions(currentEtag);
                if (precond != null) {
                        return precond
                        .cacheControl(noStore())
                        .tag(currentEtag)
                        .header("Link", new Link(Link.orders.getHref() + "/" + id, "getOrder", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                        .build();
                }
                oSA.updateOrder(id, order);
                
                return Response.noContent()
                .cacheControl(noStore())
                .header("Link", new Link(Link.orders.getHref() + "/" + id, "getOrder", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                .build();
        }
        
        @DELETE
        @Path("{id}")
        public Response deleteOrder(@Positive @PathParam("id") long id) {
                oSA.deleteOrder(id);
                return Response.noContent()
                .cacheControl(noStore())
                .header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString()))
                .build();
        }
        
        private OrderDTO addSelfLink(OrderDTO order, String rel) {
                order.setSelf(new Link(uriInfo.getBaseUri().toString() + "orders" + "/" + order.getId(), rel, "application/json"));
                return order;
        }

        private void addPaginationHeaders(Response.ResponseBuilder builder, String resourceHref, String query, String status, int offset, int size, long totalElements, boolean hasNext, boolean hasPrevious) {
                int currentPage = size > 0 ? offset / size : 0;
                long totalPages = size > 0 ? (long) Math.ceil((double) totalElements / size) : 0;
                int lastOffset = totalPages > 0 ? (int) ((totalPages - 1) * size) : 0;

                builder.header("X-Total-Count", totalElements)
                .header("X-Total-Pages", totalPages)
                .header("X-Page-Size", size)
                .header("X-Current-Page", currentPage)
                .header("X-Current-Offset", offset)
                .header("Link", new Link(buildListHref(resourceHref, 0, size, query, status), "first", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                .header("Link", new Link(buildListHref(resourceHref, lastOffset, size, query, status), "last", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));

                if (hasPrevious) {
                        builder.header("Link", new Link(buildListHref(resourceHref, Math.max(offset - size, 0), size, query, status), "prev", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
                }

                if (hasNext) {
                        builder.header("Link", new Link(buildListHref(resourceHref, offset + size, size, query, status), "next", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
                }
        }

        private String buildListHref(String resourceHref, int offset, int size, String query, String status) {
                int page = size > 0 ? offset / size : 0;
                String href = resourceHref + "?page=" + page + "&offset=" + offset + "&size=" + size;

                if (query != null && !query.isBlank()) {
                        href += "&query=" + encode(query);
                }
                if (status != null && !status.isBlank()) {
                        href += "&status=" + encode(status);
                }
                return href;
        }

        private String encode(String value) {
                return URLEncoder.encode(value, StandardCharsets.UTF_8);
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

        private Response.ResponseBuilder dashboard(
        Response.ResponseBuilder builder) {
            
            return builder
            .header("Link", Link.customers.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", Link.home.getHeaderLink(uriInfo.getBaseUri().toString()));
    }
}
