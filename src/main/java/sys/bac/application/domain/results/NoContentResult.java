package sys.bac.application.domain.results;

import java.util.Objects;

public class NoContentResult extends AbstractResult{
    
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoContentResult)) return false;
        NoContentResult res = (NoContentResult) o;
        return this.error == res.error &&
        this.errorCode == res.errorCode &&
        this.errorMessage == res.errorMessage;
    }

    @Override 
    public int hashCode() {
        return Objects.hash(error, errorCode, errorMessage);
    }
}
