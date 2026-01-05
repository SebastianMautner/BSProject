package sys.bac.application.domain.models.device;

import java.util.Objects;

import sys.bac.application.domain.models.LongId;

public class Device {

    private LongId id;
    private long customerId;
    private String serialNumber;
    private String type;
    private String brand;
    private String model;
    private String notes;

    public Device() {
        this.id = new LongId();
    }

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

    public Device(long id,
                  long customerId,
                  String serialNumber,
                  String type,
                  String brand,
                  String model,
                  String notes) {
        this.id = new LongId(id);
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

    @Override
    public boolean equals(Object dto) {
        if (this == dto) return true;
        if(!(dto instanceof Device)) return false;
        Device dto2 = (Device) dto;
        return Objects.equals(this.id, dto2.id) &&
        customerId == dto2.customerId &&
        type == dto2.type &&
        brand == dto2.brand &&
        model == dto2.model &&
        notes == dto2.notes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, type, brand, model, notes);
    }
}

