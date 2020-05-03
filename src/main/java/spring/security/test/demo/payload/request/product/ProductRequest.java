package spring.security.test.demo.payload.request.product;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProductRequest {


    @NotNull
    private final String productName;

    @NotNull
    private final String author;

    @NotNull
    private final int numberInStock;

    @NotNull
    private final String category;

    @NotNull
    private final String bookType;

    @NotNull
    private final String file;

}
