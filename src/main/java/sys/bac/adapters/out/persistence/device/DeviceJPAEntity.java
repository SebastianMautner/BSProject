package sys.bac.adapters.out.persistence.device;

import jakarta.persistence.*;

@Entity
@Table(name = "devices")
public class DeviceJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long customerId;

    @Column(nullable = false)
    private String serialNumber;

    private String type;
    private String brand;
    private String model;

    @Column(length = 2000)
    private String notes;

    public DeviceJPAEntity() {}

    public long getId() { return id; }

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
