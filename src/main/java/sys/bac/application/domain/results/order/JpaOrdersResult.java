package sys.bac.application.domain.results.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.JpaMultiResult;

public class JpaOrdersResult extends JpaMultiResult<Order> {

    public JpaOrdersResult() {
    }

    public JpaOrdersResult(List<Order> result) {
        super(result);
    }

    public JpaOrdersResult(boolean error, int errorCode) {
        super();
        this.results = new ArrayList<>();
        this.error = error;
        this.errorCode = errorCode;
    }

    public JpaOrdersResult(boolean error) {
        super();
        this.results = new ArrayList<>();
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof JpaOrdersResult)) return false;
        JpaOrdersResult res = (JpaOrdersResult) o;
        return Objects.equals(this.results, res.results) &&
        this.error == res.error &&
        this.errorCode == res.errorCode &&
        this.errorMessage == res.errorMessage;
    }

    @Override 
    public int hashCode() {
        return Objects.hash(results, error, errorCode, errorMessage);
    }
}
