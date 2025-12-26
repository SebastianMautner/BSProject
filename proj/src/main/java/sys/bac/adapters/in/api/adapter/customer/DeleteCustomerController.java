package sys.bac.adapters.in.api.adapter.customer;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.port.in.DeleteCustomerUseCase;

@Path("/customers")
public class DeleteCustomerController {

    private DeleteCustomerUseCase dCUC;

    public DeleteCustomerController(DeleteCustomerUseCase dCUC) {
        this.dCUC = dCUC;
    }

    @DELETE
    @Path("{id}")
    public void deleteCustomer(@DefaultValue("-1")@PathParam("id") long id) {
        if (id == -1) {
            throw new IllegalArgumentException("400 No Id Specified"); // not final
        }
        dCUC.deleteCustomer(new LongId(id));
    }
}
