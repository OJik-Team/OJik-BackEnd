package ojik.ojikback.infrastructure.adapter.in.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth.signup-token")
public class SignupTokenProperties {
    private String secretKey;
    private Duration expiration = Duration.ofMinutes(15);

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Duration getExpiration() {
        return expiration;
    }

    public void setExpiration(Duration expiration) {
        this.expiration = expiration;
    }
}
