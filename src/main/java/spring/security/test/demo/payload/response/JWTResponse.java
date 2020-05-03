package spring.security.test.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JWTResponse {
    private String token;
    private final String type = "Bearer";
    private String id;
    private String username;
    private String email;
    private List<String> roles;
}
