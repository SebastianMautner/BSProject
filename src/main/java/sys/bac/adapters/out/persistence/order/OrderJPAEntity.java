package sys.bac.adapters.out.persistence.order;

import java.time.LocalDate;

import jakarta.persistence.*;
import sys.bac.adapters.out.persistence.customer.CustomerJPAEntity;
import sys.bac.adapters.out.persistence.device.DeviceJPAEntity;
import sys.bac.application.domain.models.order.OrderStatus;

@Entity
@Table(name = "repair_orders")
public class OrderJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 2000)
    private String issueNotes;

    private LocalDate receivedAt;
    private LocalDate completion;

    private float costEstimation;
    private float finalCost;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deviceId")
    private DeviceJPAEntity device;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customerId")
    private CustomerJPAEntity customer;

    public OrderJPAEntity() {}

    public long getId() {
        return id;
    }

    public CustomerJPAEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerJPAEntity customer) {
        this.customer = customer;
    }

    public DeviceJPAEntity getDevice() {
        return device;
    }

    public void setDevice(DeviceJPAEntity device) {
        this.device = device;
    }

    public String getIssueNotes() {
        return issueNotes;
    }

    public void setIssueNotes(String issueNotes) {
        this.issueNotes = issueNotes;
    }

    public LocalDate getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDate receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocalDate getCompletion() {
        return completion;
    }

    public void setCompletion(LocalDate completion) {
        this.completion = completion;
    }

    public float getCostEstimation() {
        return costEstimation;
    }

    public void setCostEstimation(float costEstimation) {
        this.costEstimation = costEstimation;
    }

    public float getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(float finalCost) {
        this.finalCost = finalCost;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
