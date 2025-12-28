package sys.bac.adapters.in.api.adapter.customer;


import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ClientErrorException;

import jakarta.inject.Inject;

import java.util.List;

import sys.bac.adapters.in.api.models.CustomerDTO;

import sys.bac.application.port.in.PostCustomerUseCase;
import sys.bac.application.port.in.GetCustomersUseCase;
import sys.bac.application.port.in.GetCustomerByIdUseCase;
import sys.bac.application.port.in.PutCustomerUseCase;
import sys.bac.application.port.in.DeleteCustomerUseCase;


@Path("/customers")
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

    @GET
    @Path("/{cId}")
    @Produces(MediaType.APPLICATION_JSON) //maybe XML as well later
    public CustomerResult getCustomerById(@Positive @PathParam("customerId")long cId) {
        CustomerDTO customer;
            try {
                customer = gCBIUC.loadCustomerById(cId);
            }
            catch ( NotFoundException e) {
                throw new NotFoundException(Response.status(Response.Status.NOT_FOUND).entity(Response.Status.NOT_FOUND.getStatusCode() + "No customer with Id " + cId).build());
            }
            return new CustomerResult(customer);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON) //maybe XML as well later
    public CustomersResult getAllCustomers(@DefaultValue("") @QueryParam("query") String query) { // pagination
        List<CustomerDTO> result;
        // The Method-Path for this method is the following:
        // Controller->Use Case -(implemented by)> Service -> Repo -(implemented by)> JpaAdapter
        try {
            result = gCUC.findCustomers(query);
        }
        catch(IllegalArgumentException e) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(Response.Status.BAD_REQUEST.getStatusCode() + "Invalid 'query'").build()); // amazing line btw (only took like 5 minutes)
        }

        return new CustomersResult(result);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON) //XML
    public void postCustomer(CustomerDTO customer) {
        poCUC.createCustomer(customer);
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
