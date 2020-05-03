package spring.security.test.demo.payload.request.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DynamicValuesRequest {

    @NotNull
    private String email;
}
