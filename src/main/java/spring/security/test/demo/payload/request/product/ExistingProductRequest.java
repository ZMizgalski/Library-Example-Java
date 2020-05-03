package spring.security.test.demo.payload.request.product;

import lombok.Data;
import spring.security.test.demo.model.product.Product;


@Data
public class ExistingProductRequest {

    private final Product product;
}
