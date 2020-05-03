package spring.security.test.demo.payload.request.forgotPassword;


import lombok.Data;
import spring.security.test.demo.payload.mail.validators.FieldMatch;

import javax.validation.constraints.NotNull;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
public class ForgotPasswordDTO {

    @NotNull
    private String password;

    @NotNull
    private String confirmPassword;

    @NotNull
    private String token;


    public String getToken() {
        return token;
    }

}

