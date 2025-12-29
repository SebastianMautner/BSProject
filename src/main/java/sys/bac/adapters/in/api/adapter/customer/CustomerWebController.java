package sys.bac.adapters.in.api.adapter.customer;


import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import jakarta.inject.Inject;

import java.util.List;

import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.adapters.in.api.models.Link;
import sys.bac.application.port.in.PostCustomerUseCase;
import sys.bac.application.port.in.GetCustomersUseCase;
import sys.bac.application.port.in.GetCustomerByIdUseCase;
import sys.bac.application.port.in.PutCustomerUseCase;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.port.in.DeleteCustomerUseCase;


@Path("customers")
public class CustomerWebController {

    @Inject
    private PostCustomerUseCase poCUC;

    @Inject
    private PutCustomerUseCase puCUC;

    @Inject
    private DeleteCustomerUseCase dCUC;

    @Inject
    private  CustomerServiceAdapter cSA;

    @Context
    UriInfo uriInfo;
    
    @GET
    @Path("{cId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("customerId")long cId) { //Positive via Service Adapters
        CustomerDTO customer = cSA.getCustomerById(cId);
        return Response.ok(customer).header("Link", new Link("customers", "getAllCustomers", "application/json").getHeaderLink()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() { // pagination
        List<CustomerDTO> customers = cSA.getAllCustomers();
        customers.stream().forEach(c -> c.setSelf(new Link(uriInfo.getBaseUriBuilder()
                                                                  .path(CustomerWebController.class)
                                                                  .path(CustomerWebController.class, "getCustomerById")
                                                                  .build(c.getId()).toASCIIString(), "getCustomerWithId" + c.getId(), "application/json")));
        return Response.ok(customers).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCustomer(@Valid CustomerDTO customer) {
        
        NoContentResult result = poCUC.createCustomer(customer);
        if(result.hasError()) {
            return Response.ok(result.getMessage() + "\nTHIS IS NOT 200 OK!").build();
        }
        return Response.status(Response.Status.CREATED).header("Location", uriInfo.getRequestUriBuilder().path(Long.toString(result.getId())).build()).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateCustomer(@Positive @PathParam("id") long id, CustomerDTO customer) {
        puCUC.updateCustomer(id, customer);
    }

    @DELETE
    @Path("{id}")
    public void deleteCustomer(@PathParam("id") long id) {
        dCUC.deleteCustomer(id);
    }
}
