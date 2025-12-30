package sys.bac.application.domain.models.order;

import java.time.LocalDate;

import sys.bac.application.domain.models.LongId;

public class Order {

    private LongId id;
    private long customerId;
    private long deviceId;
    private String issueNotes;

    private LocalDate receivedAt;
    private LocalDate completion;

    private float costEstimation;
    private float finalCost;

    private OrderStatus status;

    public Order() {
        this.id = new LongId();
        this.status = OrderStatus.RECEIVED;
        this.receivedAt = LocalDate.now();
    }

    public Order(LongId id,
                 long customerId,
                 long deviceId,
                 String issueNotes,
                 LocalDate receivedAt,
                 LocalDate completion,
                 float costEstimation,
                 float finalCost,
                 OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.deviceId = deviceId;
        this.issueNotes = issueNotes;
        this.receivedAt = receivedAt;
        this.completion = completion;
        this.costEstimation = costEstimation;
        this.finalCost = finalCost;
        this.status = status;
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

    public long getDeviceId() {
        return deviceId;
    }

    public String getIssueNotes() {
        return issueNotes;
    }

    public LocalDate getReceivedAt() {
        return receivedAt;
    }

    public LocalDate getCompletion() {
        return completion;
    }

    public float getCostEstimation() {
        return costEstimation;
    }

    public float getFinalCost() {
        return finalCost;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
