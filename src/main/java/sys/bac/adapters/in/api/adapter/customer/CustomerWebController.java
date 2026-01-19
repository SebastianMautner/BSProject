package sys.bac.adapters.in.api.adapter.customer;


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
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.inject.Inject;

import java.util.stream.Collectors;
import java.util.Objects;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.adapters.in.api.models.CustomersApiResult;
import sys.bac.adapters.in.api.models.Link;

@Path("customers")
public class CustomerWebController {
    
    @Inject
    private  CustomerServiceAdapter cSA;
    
    @Context
    Request request;
    
    @Context
    private UriInfo uriInfo;
    
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
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById( @Positive @PathParam("id")long id) {
        CustomerDTO customer = cSA.getCustomerById(id);
        customer = addSelfLink(customer, "getCustomerWithId" + customer.getId());
        
        EntityTag etag = etagOf(customer);
        
        Response.ResponseBuilder precond = request.evaluatePreconditions(etag);
        if (precond != null) {
            return precond
            .cacheControl(defaultGetCacheControl())
            .tag(etag)
            .header("Link", Link.customers.getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", new Link(Link.customers.getHref() + "/" + id, "updateCustomer", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
            .header("Link", new Link(Link.customers.getHref() + "/" + id, "deleteCustomer", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
            .build();
        }
        return Response.ok(customer)
        .cacheControl(defaultGetCacheControl())
        .tag(etag)
        .header("Link", Link.customers.getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", new Link(Link.customers.getHref() + "/" + id, "updateCustomer", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", new Link(Link.customers.getHref() + "/" + id, "deleteCustomer", "application/json").getHeaderLink(uriInfo.getBaseUri().toString())).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers(@DefaultValue("") @QueryParam("query") String query,
    @PositiveOrZero @DefaultValue("0") @QueryParam("offset") int offset,
    @PositiveOrZero @DefaultValue("2") @QueryParam("size") int size) {
        size = Math.min(size, 100);
        
        CustomersApiResult customers = cSA.getCustomers(query, offset, size);
        customers.setResult(customers.getResult().stream().map(c -> addSelfLink(c, "getCustomerWithId" + c.getId())).collect(Collectors.toList()));
        Response.ResponseBuilder builder = Response.ok(customers.getResult());
        if(query.isBlank()) {
            if (customers.next() && customers.prev()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size, "prev", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
                
            } else if(customers.next()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
                
            } else if(customers.prev()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset - size) + "&size=" + size, "prev", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
            }
        }
        else {
            if (customers.next() && customers.prev()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
                
            } else if(customers.next()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
                
            } else if(customers.prev()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset - size) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
            }
            builder.header("Link", new Link(Link.customers.getHref(), "clearQuery", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()));
        }
        
        return builder
        .header("Link", new Link(Link.customers.getHref() + "?query={query}", "getNewCustomerQuery", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", Link.orders.getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", Link.devices.getHeaderLink(uriInfo.getBaseUri().toString()))
        .header("Link", new Link(Link.customers.getHref(), "createCustomer", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCustomer(@Valid CustomerDTO customer) {
        CustomerDTO result = cSA.createCustomer(customer);
        return Response.status(Response.Status.CREATED)
        .cacheControl(noStore())
        .header("Location", new Link(Link.customers.getHref() + "/" + result.getId(), "", "").getHeaderHref(uriInfo.getBaseUri().toString()))
        .build();
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@Positive @PathParam("id") long id, @Valid CustomerDTO customer, @HeaderParam("If-Match") String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) {
            return Response.status(428)
            .cacheControl(noStore())
            .entity("Missing If-Match header. Fetch the resource first (GET) and resend PUT with If-Match: <ETag>.")
            .type(MediaType.TEXT_PLAIN)
            .build();
        }
        CustomerDTO current = cSA.getCustomerById(id);
        current = addSelfLink(current, "getCustomerWithId" + current.getId());
        EntityTag currentEtag = etagOf(current);
        
        Response.ResponseBuilder precond = request.evaluatePreconditions(currentEtag);
        if (precond != null) {
            return precond
            .cacheControl(noStore())
            .tag(currentEtag)
            .header("Link", new Link(Link.customers.getHref() + "/" + id, "getCustomer", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
            .build();
        }
        cSA.updateCustomer(id, customer);
        
        return Response.noContent()
        .cacheControl(noStore())
        .header("Link", new Link(Link.customers.getHref() + "/" + id, "getCustomer", "application/json").getHeaderLink(uriInfo.getBaseUri().toString()))
        .build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteCustomer(@Positive @PathParam("id") long id) {
        cSA.deleteCustomer(id);
        return Response.noContent()
        .cacheControl(noStore())
        .header("Link", Link.customers.getHeaderLink(uriInfo.getBaseUri().toString())).build();
    }
    
    private CustomerDTO addSelfLink(CustomerDTO customer, String rel) {
        customer.setSelf(new Link(uriInfo.getBaseUri().toString() + "customers" + "/" + customer.getId(), rel, "application/json"));
        return customer;
    }
    
    @DELETE
    public void ErrorDelete() {
        throw new NotAllowedException("No DELETE for path customers/");
    }
    
    @PUT
    public void ErrorUpdate() {
        throw new NotAllowedException("No PUT for path customers/");
    }
    
    @POST
    @Path("{id}")
    public void ErrorPost() {
        throw new NotAllowedException("No POST for path customers/id");
    }
}
