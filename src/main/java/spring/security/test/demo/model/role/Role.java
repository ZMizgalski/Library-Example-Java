package spring.security.test.demo.model.role;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Roles")
public class Role {
    @Id
    private String id;

    private ERole name;
}
