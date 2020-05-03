package spring.security.test.demo.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.security.test.demo.model.order.Order;

import java.util.List;

public interface OrderRepo extends MongoRepository<Order, String> {

    Order findByEmail(String email);

    Order findOrderById(String id);

    List<Order> findAllByEmail(String email);
}
