package sys.bac.application.domain.results.order;

import java.util.ArrayList;
import java.util.List;
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
}
