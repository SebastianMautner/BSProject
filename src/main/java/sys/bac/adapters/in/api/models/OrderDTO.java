package sys.bac.adapters.in.api.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "order")
public class OrderDTO extends AbstractDataTransferObject {

    // Minimal analog zum Domain-Attribut "note"
    @NotBlank
    private String note;

    public OrderDTO() {
    }

    public OrderDTO(long id, String note) {
        this.id = id;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

