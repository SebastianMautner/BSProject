package sys.bac.adapters.in.api.adapter.customer;


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
import jakarta.ws.rs.core.MediaType;

import jakarta.inject.Inject;

import java.util.stream.Collectors;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.adapters.in.api.models.CustomersApiResult;
import sys.bac.adapters.in.api.models.Link;

@Path("customers")
public class CustomerWebController {
    
    @Inject
    private  CustomerServiceAdapter cSA;
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById( @Positive @PathParam("id")long id) {
        CustomerDTO customer = cSA.getCustomerById(id);
        customer = addSelfLink(customer, "getCustomerWithId" + customer.getId());
        return Response.ok(customer)
        .header("Link", Link.customers.getHeaderLink())
        .header("Link", new Link(Link.customers.getHref() + "/" + id, "updatePerson", "application/json").getHeaderLink())
        .header("Link", new Link(Link.customers.getHref() + "/" + id, "deletePerson", "application/json").getHeaderLink()).build();
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
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size, "prev", "application/json").getHeaderLink())
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink());
                
            } else if(customers.next()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset + size) + "&size=" + size, "next", "application/json").getHeaderLink());
                
            } else if(customers.prev()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset - size) + "&size=" + size, "prev", "application/json").getHeaderLink());
            }
        }
        else {
            if (customers.next() && customers.prev()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + Math.max(offset - size, 0) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink())
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink());
                
            } else if(customers.next()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset + size) + "&size=" + size + "&query=" + query, "next", "application/json").getHeaderLink());
                
            } else if(customers.prev()) {
                builder = builder
                .header("Link", new Link(Link.customers.getHref() + "?offset=" + (offset - size) + "&size=" + size + "&query=" + query, "prev", "application/json").getHeaderLink());
            }
            builder.header("Link", new Link(Link.customers.getHref(), "clearQuery", "application/json").getHeaderLink());
        }
        
        return builder
        .header("Link", new Link(Link.customers.getHref() + "?query={query}", "getNewCustomerQuery", "application/json").getHeaderLink())
        .header("Link", Link.orders.getHeaderLink())
        .header("Link", Link.devices.getHeaderLink())
        .header("Link", new Link(Link.customers.getHref(), "createCustomer", "application/json").getHeaderLink())
        .build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCustomer(@Valid CustomerDTO customer) {
        CustomerDTO result = cSA.createCustomer(customer);
        return Response.status(Response.Status.CREATED).header("Location", new Link(Link.customers.getHref() + "/" + result.getId(), "getCustomer", "application/json").getHeaderLink()).build();
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@Positive @PathParam("id") long id, @Valid CustomerDTO customer) {
        cSA.updateCustomer(id, customer);
        return Response.noContent().header("Link", new Link (Link.customers.getHref() + "/" + id, "getCustomer", "application/json").getHeaderLink()).build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteCustomer(@Positive @PathParam("id") long id) {
        cSA.deleteCustomer(id);
        return Response.noContent().header("Link", Link.customers.getHeaderLink()).build();
    }
    
    private CustomerDTO addSelfLink(CustomerDTO customer, String rel) {
        customer.setSelf(new Link("http://localhost:8080/" + Link.customers.getHref() + "/" + customer.getId(), rel, "application/json"));
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
