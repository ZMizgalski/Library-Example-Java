package spring.security.test.demo.model.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DynamicValues {

    @NotNull
    private int bookedNow;

    @NotNull
    private int read;
}
