package sys.bac.application.domain.results;

import java.util.List;

public class JpaMultiResult<T> extends AbstractResult{
    protected List<T> results;

    public JpaMultiResult() {
    }

    public JpaMultiResult(List<T> results) {
        this.results = results;
    }

    public List<T> getResult() {
        return results;
    }

    public void setResult(List<T> results) {
        this.results = results;
    }
    
    public boolean isEmpty() {
        return results.isEmpty();
    }
}
