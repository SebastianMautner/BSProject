package sys.bac.adapters.in.api.models;

import java.io.Serializable;

import jakarta.validation.constraints.PositiveOrZero;

public abstract class AbstractDataTransferObject implements Serializable {
    
    @PositiveOrZero
    protected long primaryId;

    @PositiveOrZero
    private long id;
    

    protected Link self;

    public long getPId() {
        return primaryId;
    }

    public void setPId(long pId) {
        primaryId = pId;
    }

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
