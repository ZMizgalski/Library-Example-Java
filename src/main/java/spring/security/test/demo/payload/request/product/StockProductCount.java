package spring.security.test.demo.payload.request.product;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StockProductCount {

    @NotNull
    private String id;

    @NotNull
    private int count;


}
