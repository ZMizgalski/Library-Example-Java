package spring.security.test.demo.payload.request.order;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderRequestSchema {

    @NotNull
    private String email;

    @NotNull
    private List<OrderSchema> orderedItems = new ArrayList<>();
}
