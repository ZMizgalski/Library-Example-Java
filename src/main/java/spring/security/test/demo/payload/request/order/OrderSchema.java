package spring.security.test.demo.payload.request.order;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderSchema {

    @NotNull
    private final String id;

    @NotNull
    private final int toOrder;

    @NotNull
    private final String productName;

    @NotNull
    private final String author;

}
