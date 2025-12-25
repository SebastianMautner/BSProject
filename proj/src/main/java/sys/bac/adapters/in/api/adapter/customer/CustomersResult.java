package sys.bac.adapters.in.api.adapter.customer;

import java.util.List;

import sys.bac.adapters.in.api.models.CustomerDTO;

public class CustomersResult {
    private List<CustomerDTO> customers;

    public CustomersResult() {}

    public CustomersResult(List<CustomerDTO> result) {
        customers = result;
    }

    public List<CustomerDTO> getCustomerDTOs() {
        return customers;
    }
}
