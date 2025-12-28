package sys.bac.adapters.in.api.adapter.customer;

import sys.bac.adapters.in.api.models.CustomerDTO;

public class CustomerResult {
    private CustomerDTO customerDTO;

    public CustomerResult() {
    }

    public CustomerResult(CustomerDTO cDTO) {
        this.customerDTO = cDTO;
    }

    public CustomerDTO getCustomerDTO() {
        return customerDTO;
    }

    public void setCustomerDTO(CustomerDTO cDTO) {
        this.customerDTO = cDTO;
    }
}
