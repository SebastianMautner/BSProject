package sys.bac.application.domain.models.order;

import java.time.LocalDate;
import java.util.Objects;

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

    public Order(long id,
                 long customerId,
                 long deviceId,
                 String issueNotes,
                 LocalDate receivedAt,
                 LocalDate completion,
                 float costEstimation,
                 float finalCost,
                 OrderStatus status) {
        this.id = new LongId(id);
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

    @Override
    public boolean equals(Object dto) {
        if (this == dto) return true;
        if(!(dto instanceof Order)) return false;
        Order dto2 = (Order) dto;
        return Objects.equals(this.id, dto2.id) &&
        customerId == dto2.customerId &&
        deviceId == dto2.deviceId &&
        issueNotes == dto2.issueNotes &&
        Objects.equals(receivedAt, dto2.receivedAt) &&
        Objects.equals(completion, dto2.completion) &&
        costEstimation == dto2.costEstimation &&
        finalCost == dto2.finalCost &&
        status == dto2.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, deviceId, issueNotes, receivedAt, completion, costEstimation, finalCost, status);
    }
}
