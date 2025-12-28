package sys.bac.adapters.in.api.models;

import java.io.Serializable;
import jakarta.validation.constraints.Positive;
import sys.bac.application.domain.models.LongId;

public abstract class AbstractDataTransferObject implements Serializable { // apparently there's also a primary id, seperate from the id, idk why tho
    @Positive
    protected LongId id;
    

    protected Link self;

    public LongId getId() {
        return id;
    }

    public void setId(LongId id) {
        this.id = id;
    }

    public Link getSelf() {
        return self;
    }

    public void setSelf(Link self) {
        this.self = self;
    }
}
