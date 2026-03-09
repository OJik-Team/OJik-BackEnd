package ojik.ojikback.infrastructure.adapter.out.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.port.out.auth.AuthTokenProvider;
import ojik.ojikback.domain.port.out.auth.model.AuthTokens;
import ojik.ojikback.infrastructure.adapter.in.config.AuthJwtProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtAuthTokenProvider implements AuthTokenProvider {
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    private final SecretKey secretKey;
    private final AuthJwtProperties properties;

    public JwtAuthTokenProvider(AuthJwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(validateSecretKey(properties).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public AuthTokens issue(Member member) {
        LocalDateTime now = LocalDateTime.now();
        return new AuthTokens(
                createToken(member, now, properties.getAccessExpiration(), "ACCESS"),
                createToken(member, now, properties.getRefreshExpiration(), "REFRESH")
        );
    }

    private String createToken(Member member, LocalDateTime issuedAt, java.time.Duration expiration, String tokenType) {
        LocalDateTime expiresAt = issuedAt.plus(expiration);
        return Jwts.builder()
                .subject(String.valueOf(member.getId()))
                .claim("nickname", member.getNickname())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(toDate(issuedAt))
                .expiration(toDate(expiresAt))
                .signWith(secretKey)
                .compact();
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

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
