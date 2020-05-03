package spring.security.test.demo.model.product;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "Products")
@Data
public class Product {

    @NotNull
    @Id
    private String id;

    @NotNull
    private String productName;

    @NotNull
    private String author;

    @NotNull
    private int numberInStock;

    @NotNull
    private int toOrder;

    @NotNull
    private int count;

    @NotNull
    private String category;

    @NotNull
    private String bookType;

    @NotNull
    private String file;
}
