package sys.bac.application.domain.models.device;

import sys.bac.application.domain.models.LongId;

public class Device {

    private LongId id;
    private long customerId;
    private String serialNumber;
    private String type;
    private String brand;
    private String model;
    private String notes;

    public Device(LongId id,
                  long customerId,
                  String serialNumber,
                  String type,
                  String brand,
                  String model,
                  String notes) {
        this.id = id;
        this.customerId = customerId;
        this.serialNumber = serialNumber;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.notes = notes;
    }

    public long getId() {
        return id.getId();
    }

    public LongId getLongId() {
        return id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getType() {
        return type;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getNotes() {
        return notes;
    }
}

