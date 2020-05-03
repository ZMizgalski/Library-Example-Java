package spring.security.test.demo.payload.request.order;

import lombok.Data;
import spring.security.test.demo.model.order.Order;

import javax.validation.constraints.NotNull;

@Data
public class OrderRequest {

    @NotNull
    private final Order order;
}
