package spring.security.test.demo.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.security.test.demo.model.user.User;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    User findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
