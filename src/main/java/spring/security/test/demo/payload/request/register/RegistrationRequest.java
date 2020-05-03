package spring.security.test.demo.payload.request.register;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
public class RegistrationRequest {
    @NotBlank
    @Size(min = 3, max = 50, message = "Username must contain at least 3 characters")
    private String username;

    @NotBlank
    @Size(min = 4, max = 100, message = "email must contain at least 4 characters")
    @Email
    private String email;

    private Set<String> roles;

    @NotBlank
    @Size(min = 8, message = "Password must contain at least 8 characters")
    private String password;
}
