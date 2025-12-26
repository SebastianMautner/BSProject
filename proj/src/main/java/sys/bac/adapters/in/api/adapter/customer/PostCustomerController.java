package sys.bac.adapters.in.api.adapter.customer;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.port.in.PostCustomerUseCase;

public class PostCustomerController {
    
    private final PostCustomerUseCase pCUC;

    private final Mapper mapper = new Mapper();

    public PostCustomerController(PostCustomerUseCase pCUC) {
        this.pCUC =pCUC;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON) //XML
    public void postCustomer(CustomerDTO customer) {
        pCUC.createCustomer(mapper.fromDTO(customer));
    }
}
