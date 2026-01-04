package sys.bac.adapters.in.api.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class DeviceDTO extends AbstractDataTransferObject {

    @Positive
    private long customerId;

    @NotBlank
    private String serialNumber;

    @NotBlank
    private String type;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    private String notes;

    public DeviceDTO() {
    }

    public DeviceDTO(long id, long customerId, String serialNumber, String type, String brand, String model, String notes) {
        this.id = id;
        this.customerId = customerId;
        this.serialNumber = serialNumber;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.notes = notes; 
    }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

