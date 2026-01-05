package sys.bac.adapters.in.api.adapter.customer;

import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.adapters.in.api.models.CustomersApiResult;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.customer.CustomerResult;
import sys.bac.application.domain.results.customer.CustomersResult;
import sys.bac.application.port.in.customer.DeleteCustomerUseCase;
import sys.bac.application.port.in.customer.GetCustomerByIdUseCase;
import sys.bac.application.port.in.customer.GetCustomersUseCase;
import sys.bac.application.port.in.customer.PostCustomerUseCase;
import sys.bac.application.port.in.customer.PutCustomerUseCase;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CustomerServiceAdapter {

    private static final Logger LOG = Logger.getLogger(CustomerServiceAdapter.class);
    
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
    
    private final Mapper mapper = new Mapper();
    
    @CacheResult(cacheName = "customer-by-id")
    public CustomerDTO getCustomerById(@CacheKey long id) {
        LOG.infof("CACHE-TEST: getCustomerById EXECUTED for id=%d", id);
        LongId cId = new LongId(id);
        CustomerResult customer = gCBIUC.loadCustomerById(cId);
        
        if (customer.getErrorCode() == 404) {
            throw new NotFoundException();
        } else if(customer.hasError()){
            throw new InternalServerErrorException(customer.getMessage());
        }
        else {
            return mapper.toDTO(customer.getResult());
        }
    }
    
    @CacheResult(cacheName = "customers-list")
    public CustomersApiResult getCustomers(String query, int offset, int size) {
        LOG.infof("CACHE-TEST: getCustomers EXECUTED for query=%s, offset=%d, size=%d", query, offset, size);
        CustomersResult customers = gCUC.findCustomers(query, offset, size);
        if(customers.hasError()) {
            throw new InternalServerErrorException(customers.getMessage());
        }
        CustomersApiResult result = new CustomersApiResult(customers.getResult().getResult()
        .stream()
        .map(mapper::toDTO)
        .collect(Collectors.toList()), customers.getResult().getTotalElements() > offset + size, offset != 0);
        return result;
    }
    
    @CacheInvalidateAll(cacheName = "customers-list")
    public CustomerDTO createCustomer(CustomerDTO customer) {
        LOG.infof("CREATE customer → cache invalidated");
        if (customer == null) {
            throw new BadRequestException("Body should contain the new Object");
        }
        CustomerResult result = poCUC.createCustomer(customer);
        if(result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
        return mapper.toDTO(result.getResult());
    }
    
    @CacheInvalidate(cacheName = "customer-by-id")
    @CacheInvalidateAll(cacheName = "customers-list")
    public void updateCustomer(@CacheKey long id, CustomerDTO customer) {
        LOG.infof("UPDATE customer id=%d → cache invalidated", id);
        LongId cId =  new LongId(id);
        if (customer == null) {
            throw new BadRequestException("Body should contain the new Object");
        }
        NoContentResult result = puCUC.updateCustomer(cId, customer);
        if (result.getErrorCode() == 404) {
            throw new NotFoundException();
        } else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
    }
    
    @CacheInvalidate(cacheName = "customer-by-id")
    @CacheInvalidateAll(cacheName = "customers-list")
    public void deleteCustomer(@CacheKey long id) {
        LOG.infof("DELETE customer id=%d → cache invalidated", id);
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