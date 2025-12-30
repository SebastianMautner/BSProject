package sys.bac.adapters.in.api.adapter.customer;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.CustomerResult;
import sys.bac.application.domain.results.CustomersResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.port.in.DeleteCustomerUseCase;
import sys.bac.application.port.in.GetCustomerByIdUseCase;
import sys.bac.application.port.in.GetCustomersUseCase;
import sys.bac.application.port.in.PostCustomerUseCase;
import sys.bac.application.port.in.PutCustomerUseCase;

@ApplicationScoped
public class CustomerServiceAdapter {
    
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
    
    private Mapper mapper = new Mapper();
    
    public CustomerDTO getCustomerById(long id) {
        LongId cId = new LongId(id);
        CustomerResult customer = gCBIUC.loadCustomerById(cId);
        
        if (customer.isEmpty()) {
            throw new NotFoundException();
        } else if(customer.hasError()){
            throw new InternalServerErrorException(customer.getMessage());
        }
        else {
            return mapper.toDTO(customer.getResult());
        }
    }
    
    public List<CustomerDTO> getCustomers(String query) {
        CustomersResult customers = gCUC.findCustomers(query);
        if(customers.hasError()) {
            throw new InternalServerErrorException(customers.getMessage());
        }
        return customers.getResult().stream().map(customer -> mapper.toDTO(customer)).collect(Collectors.toList());
    }
    
    public CustomerDTO createCustomer(CustomerDTO customer) {
        CustomerResult result = poCUC.createCustomer(customer);
        if(result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
        else {
            return mapper.toDTO(result.getResult());
        }
    }
    
    public CustomerDTO updateCustomer(long id, CustomerDTO customer) {
        LongId cId =  new LongId(id);
        CustomerResult result = puCUC.updateCustomer(cId, customer);
        if (result.isEmpty()) {
            throw new NotFoundException();
        } else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        } else {
            return mapper.toDTO(result.getResult());
        }
    }
    
    public void deleteCustomer(long id) {
        LongId cId = new LongId(id);
        NoContentResult result = dCUC.deleteCustomer(cId);
        if (result.getErrorCode() == 404) {
            throw new NotFoundException();
        } 
        else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
    }
}
