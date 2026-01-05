package sys.bac.application.domain.results;

import sys.bac.application.domain.models.Page;

public class MultiResult<T> extends AbstractResult {
    protected Page<T> result;
    
    public MultiResult() {
    }

    public MultiResult(Page<T> result) {
        this.result = result;
    }
    
    public boolean isEmpty() {
        return result.isEmpty();
    }

    public void setResult(Page<T> result) {
        this.result = result;
    }

    public Page<T> getResult() {
        return result;
    }
}
