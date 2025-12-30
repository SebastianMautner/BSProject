package sys.bac.adapters.in.api.models;

import java.io.Serializable;
import jakarta.validation.constraints.PositiveOrZero;


public abstract class AbstractDataTransferObject implements Serializable {
    @PositiveOrZero
    protected long id;
    

    protected Link self;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Link getSelf() {
        return self;
    }

    public void setSelf(Link self) {
        this.self = self;
    }
}
