package sys.bac.adapters.in.api.models;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import sys.bac.application.domain.models.order.OrderStatus;

public class OrderDTO extends AbstractDataTransferObject {

    @Positive
    private long customerId;

    @Positive
    private long deviceId;

    @NotBlank
    private String issueNotes;

    @NotNull
    private LocalDate receivedAt;

    private LocalDate completion;

    @PositiveOrZero
    private float costEstimation;

    @PositiveOrZero
    private float finalCost;

    @NotNull
    private OrderStatus status;

    public OrderDTO() {
    }

    public OrderDTO(long id, long customerId, long deviceId, String issueNotes, LocalDate receivedAt, float costEstimation, OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.deviceId = deviceId;
        this.receivedAt = receivedAt;
        this.issueNotes = issueNotes;
        this.costEstimation = costEstimation;
        this.status = status;
    }

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

    @Override
    public boolean equals(Object dto) {
        if (this == dto) return true;
        if(!(dto instanceof OrderDTO)) return false;
        OrderDTO dto2 = (OrderDTO) dto;
        return id == dto2.id &&
        customerId == dto2.customerId &&
        deviceId == dto2.deviceId &&
        issueNotes == dto2.issueNotes &&
        receivedAt == dto2.receivedAt &&
        completion == dto2.completion &&
        costEstimation == dto2.costEstimation &&
        finalCost == dto2.finalCost &&
        status == dto2.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, deviceId, issueNotes, receivedAt, completion, costEstimation, finalCost, status);
    }
}


