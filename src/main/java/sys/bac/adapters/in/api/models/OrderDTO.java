package sys.bac.adapters.in.api.models;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import sys.bac.application.domain.models.order.OrderStatus;

public class OrderDTO extends AbstractDataTransferObject {

    @NotBlank
    private long customerId;
    @NotBlank
    private long deviceId;
    @NotBlank
    private String issueNotes;

    @NotBlank
    private LocalDate receivedAt;

    private LocalDate completion;

    @NotBlank
    private float costEstimation;
    
    private float finalCost;

    private OrderStatus status;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
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


