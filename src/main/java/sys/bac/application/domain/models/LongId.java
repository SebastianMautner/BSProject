package sys.bac.application.domain.models;

public class LongId {
    private long id;

    public LongId() {
    }

    public LongId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;        
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongId)) return false;
        LongId id = (LongId) o;
        return this.id == id.id;
    }
}
