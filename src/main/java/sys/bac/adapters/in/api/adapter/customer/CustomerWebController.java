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
    public Response getCustomerById(@PathParam("customerId")long id) { //Positive via Service Adapters
        CustomerDTO customer = cSA.getCustomerById(id);
        return Response.ok(customer).header("Link", new Link("customers", "getAllCustomers", "application/json").getHeaderLink()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers(@DefaultValue("") @QueryParam("q") String query,
                                 @PositiveOrZero @DefaultValue("0") @QueryParam("offset") int offset,
                                 @PositiveOrZero @DefaultValue("2") @QueryParam("size") int size) { // pagination
        List<CustomerDTO> customers = cSA.getCustomers(query);
        customers.stream().forEach(c -> c.setSelf(new Link(uriInfo.getBaseUriBuilder()
                                                                  .path(CustomerWebController.class)
                                                                  .path(CustomerWebController.class, "getCustomerById")
                                                                  .build(c.getId()).toASCIIString(), "getCustomerWithId" + c.getId(), "application/json")));
        return Response.ok(customers).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCustomer(@Valid CustomerDTO customer) {
        CustomerDTO result = cSA.createCustomer(customer);
        return Response.status(Response.Status.CREATED).header("Location", uriInfo.getRequestUriBuilder().path(Long.toString(result.getId())).build()).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@Positive @PathParam("id") long id, CustomerDTO customer) {
        CustomerDTO result = cSA.updateCustomer(id, customer);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteCustomer(@PathParam("id") long id) {
        cSA.deleteCustomer(id);
        return Response.noContent().build();
    }
}
