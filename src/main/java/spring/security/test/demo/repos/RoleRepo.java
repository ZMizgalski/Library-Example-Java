package spring.security.test.demo.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.security.test.demo.model.role.ERole;
import spring.security.test.demo.model.role.Role;

import java.util.Optional;

public interface RoleRepo extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
