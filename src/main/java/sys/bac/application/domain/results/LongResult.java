package sys.bac.application.domain.results;

import java.util.Objects;

public class LongResult extends SingleResult<Long> {
    public LongResult() {
    }

    public LongResult(Long i) {
        super(i);
    }

    public LongResult(boolean error, int errorCode) {
        super();
        this.result = -1L;
        this.error = error;
        this.errorCode = errorCode;
    }

    public LongResult(boolean error) {
        super();
        this.result = -1L;
        this.error = error;
    }

    @Override
    public boolean equals(Object dto) {
        if (this == dto) return true;
        if(!(dto instanceof LongResult)) return false;
        LongResult dto2 = (LongResult) dto;
        return Objects.equals(this.result, dto2.result) &&
        error == dto2.error &&
        errorCode == dto2.errorCode &&
        errorMessage == dto2.errorMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, error, errorCode, errorMessage);
    }
}
