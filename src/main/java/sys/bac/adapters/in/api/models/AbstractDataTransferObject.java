package sys.bac.adapters.in.api.models;

import java.io.Serializable;
import jakarta.validation.constraints.Positive;


public abstract class AbstractDataTransferObject implements Serializable { // apparently there's also a primary id, seperate from the id, idk why tho
    @Positive
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
