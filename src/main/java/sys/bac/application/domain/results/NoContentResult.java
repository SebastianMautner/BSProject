package sys.bac.application.domain.results;

public class NoContentResult extends AbstractResult{

    private long id;
    
    public NoContentResult() {}

    public NoContentResult(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public NoContentResult(boolean error) {
        super();
        this.error = error;
    }

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
