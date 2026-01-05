package sys.bac.application.domain.results;

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
}
