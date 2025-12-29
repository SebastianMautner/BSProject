package sys.bac.application.domain.models.order;

public enum OrderStatus {
    RECEIVED,
    IN_REPAIR,
    WAITING_FOR_PARTS,
    COMPLETED,
    CANCELLED
}
