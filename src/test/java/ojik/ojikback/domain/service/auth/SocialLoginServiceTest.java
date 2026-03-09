package ojik.ojikback.domain.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.SocialAccount;
import ojik.ojikback.domain.entity.Team;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.in.auth.command.SocialLoginCommand;
import ojik.ojikback.domain.port.in.auth.result.SocialLoginResult;
import ojik.ojikback.domain.port.out.auth.AuthTokenProvider;
import ojik.ojikback.domain.port.out.auth.SignupSessionProvider;
import ojik.ojikback.domain.port.out.auth.SocialAccountReader;
import ojik.ojikback.domain.port.out.auth.SocialAuthClient;
import ojik.ojikback.domain.port.out.auth.model.AuthTokens;
import ojik.ojikback.domain.port.out.auth.model.SignupSession;
import ojik.ojikback.domain.port.out.auth.model.SocialUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SocialLoginServiceTest {
    private static final java.time.LocalDateTime FIXED_DATE_TIME = java.time.LocalDateTime.of(2026, 3, 9, 12, 0, 0);

    @Test
    @DisplayName("기존 회원이면 JWT를 발급하고 회원 정보를 반환한다")
    void loginExistingMember() {
        // given
        Member member = member(1L, "다영", 3L);
        SocialAccount socialAccount = socialAccount(member, SocialProvider.KAKAO, "123456");
        SocialUserInfo socialUserInfo = new SocialUserInfo(SocialProvider.KAKAO, "123456");

        SocialLoginService service = new SocialLoginService(
                List.of(new StubSocialAuthClient(SocialProvider.KAKAO, socialUserInfo)),
                (provider, providerUserId) -> Optional.of(socialAccount),
                new UnusedSignupSessionProvider(),
                issuedMember -> new AuthTokens("jwt-access", "jwt-refresh")
        );

        // when
        SocialLoginResult result = service.login(new SocialLoginCommand(SocialProvider.KAKAO, "auth-code"));

        // then
        assertThat(result.isNew()).isFalse();
        assertThat(result.accessToken()).isEqualTo("jwt-access");
        assertThat(result.refreshToken()).isEqualTo("jwt-refresh");
        assertThat(result.signupToken()).isNull();
        assertThat(result.member()).isNotNull();
        assertThat(result.member().id()).isEqualTo(1L);
        assertThat(result.member().nickname()).isEqualTo("다영");
        assertThat(result.member().favoriteTeamId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("신규 회원이면 signupToken을 발급한다")
    void loginNewMember() {
        // given
        SocialUserInfo socialUserInfo = new SocialUserInfo(SocialProvider.KAKAO, "999999");

        SocialLoginService service = new SocialLoginService(
                List.of(new StubSocialAuthClient(SocialProvider.KAKAO, socialUserInfo)),
                (provider, providerUserId) -> Optional.empty(),
                new StubSignupSessionProvider("signup-token"),
                new UnusedAuthTokenProvider()
        );

        // when
        SocialLoginResult result = service.login(new SocialLoginCommand(SocialProvider.KAKAO, "auth-code"));

        // then
        assertThat(result.isNew()).isTrue();
        assertThat(result.signupToken()).isEqualTo("signup-token");
        assertThat(result.accessToken()).isNull();
        assertThat(result.refreshToken()).isNull();
        assertThat(result.member()).isNull();
    }

    @Test
    @DisplayName("지원하지 않는 provider면 INVALID_PROVIDER 예외를 던진다")
    void loginFailWhenProviderNotSupported() {
        // given
        SocialLoginService service = new SocialLoginService(
                List.of(new StubSocialAuthClient(SocialProvider.KAKAO, new SocialUserInfo(SocialProvider.KAKAO, "123456"))),
                (provider, providerUserId) -> Optional.empty(),
                new StubSignupSessionProvider("signup-token"),
                new UnusedAuthTokenProvider()
        );

        // when // then
        assertThatThrownBy(() -> service.login(new SocialLoginCommand(SocialProvider.NAVER, "auth-code")))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_PROVIDER);
    }

    private Member member(Long memberId, String nickname, Long favoriteTeamId) {
        Team team = Team.create("LG Twins", "LG", "#000000");
        ReflectionTestUtils.setField(team, "id", favoriteTeamId);

        Member member = Member.create(nickname, team, null, FIXED_DATE_TIME);
        ReflectionTestUtils.setField(member, "id", memberId);
        return member;
    }

    private SocialAccount socialAccount(Member member, SocialProvider provider, String providerUserId) {
        SocialAccount socialAccount = SocialAccount.create(member, provider, providerUserId, FIXED_DATE_TIME);
        ReflectionTestUtils.setField(socialAccount, "id", 10L);
        return socialAccount;
    }

    private record StubSocialAuthClient(SocialProvider provider, SocialUserInfo socialUserInfo) implements SocialAuthClient {
        @Override
        public SocialUserInfo fetchUserInfo(String authCode) {
            return socialUserInfo;
        }
    }

    private record StubSignupSessionProvider(String token) implements SignupSessionProvider {
        @Override
        public String create(SocialUserInfo userInfo) {
            return token;
        }

        @Override
        public SignupSession get(String token) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class UnusedSignupSessionProvider implements SignupSessionProvider {
        @Override
        public String create(SocialUserInfo userInfo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SignupSession get(String token) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class UnusedAuthTokenProvider implements AuthTokenProvider {
        @Override
        public AuthTokens issue(Member member) {
            throw new UnsupportedOperationException();
        }
    }
}
