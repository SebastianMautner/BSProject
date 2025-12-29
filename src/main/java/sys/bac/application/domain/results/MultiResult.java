package sys.bac.application.domain.results;

import java.util.List;

public class MultiResult<T> extends AbstractResult {
    List<T> result;
    
    public MultiResult() {
    }

    public MultiResult(List<T> result) {
        this.result = result;
    }
    public boolean isEmpty() {
        return result.isEmpty();
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public List<T> getResult() {
        return result;
    }
}
