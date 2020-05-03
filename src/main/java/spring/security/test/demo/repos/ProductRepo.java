package spring.security.test.demo.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.security.test.demo.model.product.Product;


public interface ProductRepo extends MongoRepository<Product, String> {

    boolean existsByProductName(String productName);

    Product findProductById(String id);


}
