package sys.bac.application.domain.results.order;

import java.util.List;
import sys.bac.application.domain.models.order.Order;
import sys.bac.application.domain.results.JpaMultiResult;

public class JpaOrdersResult extends JpaMultiResult<Order> {

    public JpaOrdersResult() {
    }

    public JpaOrdersResult(List<Order> result) {
        super(result);
    }
}
