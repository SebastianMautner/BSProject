package sys.bac.application.domain.results;

public class NoContentResult extends AbstractResult{

    private long id;
    
    public NoContentResult() {}

    public boolean isEmpty() {
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
