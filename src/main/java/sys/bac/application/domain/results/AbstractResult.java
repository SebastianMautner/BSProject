package sys.bac.application.domain.results;

public abstract class AbstractResult {
    
    private boolean error;
    
    private int errorCode;

    private String errorMessage;

    protected AbstractResult() {
        error = false;
    }

    public abstract boolean isEmpty();

    public boolean hasError() {
        return error;
    }

    public void setError() {
        error = true;
    }

    public void setError(int errorCode, String errorMessage) {
        setError();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return errorMessage;
    }
}
