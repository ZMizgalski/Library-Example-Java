package spring.security.test.demo.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.security.test.demo.model.token.ResetPasswordToken;

import java.util.Date;

public interface PasswordTokenRepo extends MongoRepository<ResetPasswordToken, String> {

    ResetPasswordToken findByToken(String token);

    boolean existsByEmail(String userEmail);

    ResetPasswordToken findByEmail(String resetPasswordEmail);

    void deleteByEmail(String email);

    void deleteByExpiryDateLessThan(Date now);
}

