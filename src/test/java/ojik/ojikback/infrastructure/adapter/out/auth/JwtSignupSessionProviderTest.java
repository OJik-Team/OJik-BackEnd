package ojik.ojikback.infrastructure.adapter.out.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
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
        Instant now = Instant.parse("2026-03-09T12:00:00Z");
        JwtSignupSessionProvider provider = new JwtSignupSessionProvider(properties(), fixedClock(now));
        SocialUserInfo userInfo = new SocialUserInfo(SocialProvider.KAKAO, "123456");

        // when
        String token = provider.create(userInfo);
        SignupSession session = provider.get(token);

        // then
        assertThat(session.provider()).isEqualTo(SocialProvider.KAKAO);
        assertThat(session.providerUserId()).isEqualTo("123456");
        assertThat(session.issuedAt()).isEqualTo(now);
        assertThat(session.expiresAt()).isEqualTo(now.plusSeconds(900));
    }

    @Test
    @DisplayName("만료된 signupToken은 SIGNUP_TOKEN_EXPIRED 예외를 던진다")
    void getFailWhenExpired() {
        // given
        Instant now = Instant.parse("2026-03-09T12:00:00Z");
        JwtSignupSessionProvider provider = new JwtSignupSessionProvider(properties(), fixedClock(now.plusSeconds(901)));
        String expiredToken = Jwts.builder()
                .claim("provider", SocialProvider.KAKAO.name())
                .claim("providerUserId", "123456")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900)))
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
        Instant now = Instant.parse("2026-03-09T12:00:00Z");
        JwtSignupSessionProvider provider = new JwtSignupSessionProvider(properties(), fixedClock(now));
        String tamperedToken = Jwts.builder()
                .claim("provider", SocialProvider.KAKAO.name())
                .claim("providerUserId", "123456")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900)))
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
        Instant now = Instant.parse("2026-03-09T12:00:00Z");
        JwtSignupSessionProvider provider = new JwtSignupSessionProvider(properties(), fixedClock(now));
        String token = Jwts.builder()
                .claim("provider", SocialProvider.KAKAO.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900)))
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

    private Clock fixedClock(Instant now) {
        return Clock.fixed(now, ZoneOffset.UTC);
    }
}
