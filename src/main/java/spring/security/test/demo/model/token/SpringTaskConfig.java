package spring.security.test.demo.model.token;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.security.test.demo.repos.PasswordTokenRepo;

import java.time.Instant;
import java.util.Date;


@EnableScheduling
public class SpringTaskConfig {


    @Service
    @Transactional
    public static class TokensPurgeTask {

        @Autowired
        private PasswordTokenRepo verificationTokenRepository;

        @Scheduled(fixedRate = 360000000)
        public void purgeExpired() {
            Date now = Date.from(Instant.now());
            verificationTokenRepository.deleteByExpiryDateLessThan(now);
        }
    }
}
