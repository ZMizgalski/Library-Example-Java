package spring.security.test.demo.model.order;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import spring.security.test.demo.payload.request.order.OrderSchema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Data
@Document(collection = "Orders")
public class Order {

    @NotNull
    private String id;

    @NotNull
    private String email;

    @NotNull
    private List<OrderSchema> orderedItems = new ArrayList<>();


}
