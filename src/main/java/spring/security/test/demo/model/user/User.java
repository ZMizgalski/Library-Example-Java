package spring.security.test.demo.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import spring.security.test.demo.model.role.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "User")
public class User {

    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    private String password;

    @DBRef
    private Set<Role> roles = new HashSet<>();

    @NotBlank
    private int bookedNow;

    @NotBlank
    private int read;


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
