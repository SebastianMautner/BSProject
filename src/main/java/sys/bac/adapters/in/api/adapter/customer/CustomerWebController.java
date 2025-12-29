package sys.bac.adapters.in.api.adapter.customer;


import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.DefaultValue;
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
    private GetCustomerByIdUseCase gCBIUC;

    @Inject
    private GetCustomersUseCase gCUC;

    @Inject
    private PostCustomerUseCase poCUC;

    @Inject
    private PutCustomerUseCase puCUC;

    @Inject
    private DeleteCustomerUseCase dCUC;

    @Context
    UriInfo uriInfo;
    
    @GET
    @Path("{cId}")
    @Produces(MediaType.APPLICATION_JSON) //maybe XML as well later
    public CustomerResult getCustomerById(@Positive @PathParam("customerId")long cId) {
        CustomerDTO customer;
        customer = gCBIUC.loadCustomerById(cId);
        return new CustomerResult(customer);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON) //maybe XML as well later
    public Response getAllCustomers() { // pagination
        List<CustomerDTO> customers;
            customers = gCUC.findCustomers();
        customers.stream().forEach(c -> c.setSelf(new Link(uriInfo.getBaseUriBuilder()
                                                                  .path(CustomerWebController.class)
                                                                  .build(c.getId()).toASCIIString(), "getCustomerWithId" + c.getId(), "application/json")));
        return Response.ok(customers).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON) //XML
    public Response postCustomer(@Valid CustomerDTO customer) {
        
        NoContentResult result = poCUC.createCustomer(customer);
        if(result.hasError()) {
            return Response.ok(result.getMessage() + "\nTHIS IS NOT 200 OK!").build();
        }
        return Response.status(Response.Status.CREATED).header("Location", uriInfo.getRequestUriBuilder().path(Long.toString(result.getId())).build()).build();
        //return Response.status(Response.Status.CREATED).header("Location:" uriInfo.getAbsolutePath().path(Long.toString(0))).build(); 
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON) //XML
    public void updateCustomer(@Positive @PathParam("id") long id, CustomerDTO customer) {
        puCUC.updateCustomer(id, customer);
    }

    @DELETE
    @Path("{id}")
    public void deleteCustomer(@DefaultValue("-1")@PathParam("id") long id) {
        if (id == -1) {
            throw new IllegalArgumentException("400 No Id Specified"); // not final
        }
        dCUC.deleteCustomer(id);
    }
}
