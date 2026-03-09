package ojik.ojikback.infrastructure.adapter.out.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.out.auth.SignupSessionProvider;
import ojik.ojikback.domain.port.out.auth.model.SignupSession;
import ojik.ojikback.domain.port.out.auth.model.SocialUserInfo;
import ojik.ojikback.infrastructure.adapter.in.config.SignupTokenProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtSignupSessionProvider implements SignupSessionProvider {
    private static final String PROVIDER_CLAIM = "provider";
    private static final String PROVIDER_USER_ID_CLAIM = "providerUserId";

    private final SecretKey secretKey;
    private final Duration expiration;

    public JwtSignupSessionProvider(SignupTokenProperties properties) {
        this.secretKey = Keys.hmacShaKeyFor(validateSecretKey(properties).getBytes(StandardCharsets.UTF_8));
        this.expiration = properties.getExpiration();
    }

    @Override
    public String create(SocialUserInfo userInfo) {
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiresAt = issuedAt.plus(expiration);

        return Jwts.builder()
                .claim(PROVIDER_CLAIM, userInfo.provider().name())
                .claim(PROVIDER_USER_ID_CLAIM, userInfo.providerUserId())
                .issuedAt(toDate(issuedAt))
                .expiration(toDate(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public SignupSession get(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .clock(() -> toDate(LocalDateTime.now()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new SignupSession(
                    parseProvider(claims.get(PROVIDER_CLAIM, String.class)),
                    parseProviderUserId(claims.get(PROVIDER_USER_ID_CLAIM, String.class)),
                    toLocalDateTime(claims.getIssuedAt()),
                    toLocalDateTime(claims.getExpiration())
            );
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.SIGNUP_TOKEN_EXPIRED, "signupToken이 만료되었습니다.", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.INVALID_SIGNUP_TOKEN, "유효하지 않은 signupToken입니다.", e);
        }
    }

    private static String validateSecretKey(SignupTokenProperties properties) {
        String secretKey = properties.getSecretKey();

        if (!StringUtils.hasText(secretKey)) {
            throw new IllegalStateException("auth.signup-token.secret-key 설정이 필요합니다.");
        }

        if (secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("auth.signup-token.secret-key는 최소 32바이트 이상이어야 합니다.");
        }

        return secretKey;
    }

    private SocialProvider parseProvider(String provider) {
        if (!StringUtils.hasText(provider)) {
            throw new AuthException(AuthErrorCode.INVALID_SIGNUP_TOKEN, "provider 클레임이 없습니다.");
        }

        try {
            return SocialProvider.valueOf(provider);
        } catch (IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.INVALID_SIGNUP_TOKEN, "provider 클레임이 올바르지 않습니다.", e);
        }
    }

    private String parseProviderUserId(String providerUserId) {
        if (!StringUtils.hasText(providerUserId)) {
            throw new AuthException(AuthErrorCode.INVALID_SIGNUP_TOKEN, "providerUserId 클레임이 없습니다.");
        }
        return providerUserId;
    }

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
