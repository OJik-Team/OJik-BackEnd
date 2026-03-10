package ojik.ojikback.infrastructure.adapter.out.auth;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import ojik.ojikback.infrastructure.adapter.in.config.AuthJwtProperties;
import org.springframework.util.StringUtils;

public final class JwtTokenSupport {
    public static final String TOKEN_TYPE_CLAIM = "tokenType";
    public static final String NICKNAME_CLAIM = "nickname";
    public static final String ACCESS_TOKEN_TYPE = "ACCESS";
    public static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private JwtTokenSupport() {
    }

    public static SecretKey createSecretKey(AuthJwtProperties properties) {
        return Keys.hmacShaKeyFor(validateSecretKey(properties).getBytes(StandardCharsets.UTF_8));
    }

    private static String validateSecretKey(AuthJwtProperties properties) {
        String secretKey = properties.getSecretKey();

        if (!StringUtils.hasText(secretKey)) {
            throw new IllegalStateException("auth.jwt.secret-key 설정이 필요합니다.");
        }

        if (secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("auth.jwt.secret-key는 최소 32바이트 이상이어야 합니다.");
        }

        return secretKey;
    }
}
