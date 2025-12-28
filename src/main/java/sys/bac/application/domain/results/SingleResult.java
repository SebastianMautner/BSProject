package sys.bac.application.domain.results;

public class SingleResult<T> extends AbstractResult {
    private T result;

    public SingleResult() {
    }

    public SingleResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public boolean isEmpty() {
        return result == null;
    }
}
