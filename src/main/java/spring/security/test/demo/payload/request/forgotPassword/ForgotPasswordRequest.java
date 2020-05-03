package spring.security.test.demo.payload.request.forgotPassword;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data

public class ForgotPasswordRequest {

    @NotNull
    private String email;


}
