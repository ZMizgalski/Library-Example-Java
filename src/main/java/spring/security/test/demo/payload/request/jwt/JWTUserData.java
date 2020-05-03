package spring.security.test.demo.payload.request.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class JWTUserData {

    @NotBlank
    private String token;

}
