package sys.bac.adapters.in.api.adapter.customer;


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
import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.adapters.in.api.models.Link;

@Path("customers")
public class CustomerWebController {

    @Inject
    private  CustomerServiceAdapter cSA;

    @Context
    UriInfo uriInfo;
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id")long id) { //Positive via Service Adapters
        CustomerDTO customer = cSA.getCustomerById(id);
        customer = addSelfLink(customer, "getCustomerWithId" + customer.getId());
        return Response.ok(customer).header("Link", Link.customers.getHeaderLink())
                                    .header("Link", new Link(Link.customers.getHref() + "/" + id, "updatePerson", "application/json").getHeaderLink())
                                    .header("Link", new Link(Link.customers.getHref() + "/" + id, "deletePerson", "application/json").getHeaderLink()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers(@DefaultValue("") @QueryParam("q") String query,
                                 @PositiveOrZero @DefaultValue("0") @QueryParam("offset") int offset,
                                 @PositiveOrZero @DefaultValue("2") @QueryParam("size") int size) { // pagination
        List<CustomerDTO> customers = cSA.getCustomers(query);
        customers = customers.stream().map(c -> addSelfLink(c, "getOrderWithId" + c.getId())).collect(Collectors.toList());
        return Response.ok(customers).header("Link", Link.devices.getHeaderLink())
                                     .header("Link", Link.orders.getHeaderLink())
                                     .header("Link", new Link(Link.customers.getHref(), "createCustomer", "application/json").getHeaderLink()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCustomer(@Valid CustomerDTO customer) {
        CustomerDTO result = cSA.createCustomer(customer);
        return Response.status(Response.Status.CREATED).header("Location", new Link(Link.customers.getHref() + "/" + result.getId(), "getPerson", "application/json").getHeaderLink()).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@Positive @PathParam("id") long id, CustomerDTO customer) {
        CustomerDTO result = cSA.updateCustomer(id, customer);
        return Response.noContent().header("Link", new Link (Link.customers.getHref() + "/" + result.getId(), "getCustomer", "application/json").getHeaderLink()).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteCustomer(@PathParam("id") long id) {
        cSA.deleteCustomer(id);
        return Response.noContent().header("Link", Link.customers.getHeaderLink()).build();
    }

    private CustomerDTO addSelfLink(CustomerDTO customer, String rel) {
        customer.setSelf(new Link("http://localhost:8080/" + Link.customers.getHref() + "/" + customer.getId(), rel, "application/json"));
        return customer;
    }
}
