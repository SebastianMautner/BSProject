package sys.bac.adapters.out.persistence.order;

import jakarta.persistence.*;

@Entity
@Table(name = "OrderTable")
public class OrderJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;

    @Column(nullable = false)
    private String note;

    public OrderJPAEntity() {
    }

    public OrderJPAEntity(String note) {
        this.note = note;
    }

    public long getId() {
        return orderId;
    }

    public void setId(long id) {
        this.orderId = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
