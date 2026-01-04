package sys.bac.application.domain.results;

public class SingleResult<T> extends AbstractResult {
    protected T result;

    public SingleResult() {
    }

    public SingleResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isEmpty() {
        return result == null;
    }
}
