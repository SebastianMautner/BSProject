package sys.bac.application.domain.models.order;

import sys.bac.application.domain.models.LongId;

public class Order {

    private LongId orderId;

    private String note;

    public Order() {
        this.orderId = new LongId();
    }

    public Order(LongId id, String note) {
        this.orderId = id;
        this.note = note;
    }

    public Order(long id, String note) {
        this.orderId = new LongId(id);
        this.note = note;
    }

    public long getOrderId() {
        return orderId.getId();
    }

    public LongId getLongId() {
        return orderId;
    }

    public void setOId(long orderId) {
        this.orderId = new LongId(orderId);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
