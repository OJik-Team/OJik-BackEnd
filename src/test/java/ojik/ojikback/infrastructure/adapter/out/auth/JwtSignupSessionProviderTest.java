package ojik.ojikback.infrastructure.adapter.out.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.out.auth.model.SignupSession;
import ojik.ojikback.domain.port.out.auth.model.SocialUserInfo;
import ojik.ojikback.infrastructure.adapter.in.config.SignupTokenProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtSignupSessionProviderTest {
    private static final String SECRET_KEY = "signup-token-secret-key-must-be-at-least-32bytes";

    @Test
    @DisplayName("signupToken을 발급하고 다시 조회할 수 있다")
    void createAndGetSuccess() {
        // given
        JwtSignupSessionProvider provider = new JwtSignupSessionProvider(properties());
        SocialUserInfo userInfo = new SocialUserInfo(SocialProvider.KAKAO, "123456");
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // when
        String token = provider.create(userInfo);
        SignupSession session = provider.get(token);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // then
        assertThat(session.provider()).isEqualTo(SocialProvider.KAKAO);
        assertThat(session.providerUserId()).isEqualTo("123456");
        assertThat(session.issuedAt()).isAfterOrEqualTo(before);
        assertThat(session.issuedAt()).isBeforeOrEqualTo(after);
        assertThat(session.expiresAt()).isEqualTo(session.issuedAt().plusMinutes(15));
    }

    @Test
    @DisplayName("만료된 signupToken은 SIGNUP_TOKEN_EXPIRED 예외를 던진다")
    void getFailWhenExpired() {
        // given
        LocalDateTime now = LocalDateTime.now().minusMinutes(16);
        JwtSignupSessionProvider provider = new JwtSignupSessionProvider(properties());
        String expiredToken = Jwts.builder()
                .claim("provider", SocialProvider.KAKAO.name())
                .claim("providerUserId", "123456")
                .issuedAt(java.sql.Timestamp.valueOf(now))
                .expiration(java.sql.Timestamp.valueOf(now.plusMinutes(15)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();

        // when // then
        assertThatThrownBy(() -> provider.get(expiredToken))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.SIGNUP_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("변조된 signupToken은 INVALID_SIGNUP_TOKEN 예외를 던진다")
    void getFailWhenTokenTampered() {
        // given
        LocalDateTime now = LocalDateTime.now();
        JwtSignupSessionProvider provider = new JwtSignupSessionProvider(properties());
        String tamperedToken = Jwts.builder()
                .claim("provider", SocialProvider.KAKAO.name())
                .claim("providerUserId", "123456")
                .issuedAt(java.sql.Timestamp.valueOf(now))
                .expiration(java.sql.Timestamp.valueOf(now.plusMinutes(15)))
                .signWith(Keys.hmacShaKeyFor("different-signup-token-secret-key-12345".getBytes(StandardCharsets.UTF_8)))
                .compact();

        // when // then
        assertThatThrownBy(() -> provider.get(tamperedToken))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_SIGNUP_TOKEN);
    }

    @Test
    @DisplayName("providerUserId 클레임이 없으면 INVALID_SIGNUP_TOKEN 예외를 던진다")
    void getFailWhenProviderUserIdMissing() {
        // given
        LocalDateTime now = LocalDateTime.now();
        JwtSignupSessionProvider provider = new JwtSignupSessionProvider(properties());
        String token = Jwts.builder()
                .claim("provider", SocialProvider.KAKAO.name())
                .issuedAt(java.sql.Timestamp.valueOf(now))
                .expiration(java.sql.Timestamp.valueOf(now.plusMinutes(15)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();

        // when // then
        assertThatThrownBy(() -> provider.get(token))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_SIGNUP_TOKEN);
    }

    private SignupTokenProperties properties() {
        SignupTokenProperties properties = new SignupTokenProperties();
        properties.setSecretKey(SECRET_KEY);
        properties.setExpiration(java.time.Duration.ofMinutes(15));
        return properties;
    }
}
