package ojik.ojikback.domain.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Optional;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.SocialAccount;
import ojik.ojikback.domain.entity.Team;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.in.auth.command.SocialSignupCommand;
import ojik.ojikback.domain.port.in.auth.result.SocialSignupResult;
import ojik.ojikback.domain.port.out.auth.AuthTokenProvider;
import ojik.ojikback.domain.port.out.auth.MemberNicknameChecker;
import ojik.ojikback.domain.port.out.auth.MemberWriter;
import ojik.ojikback.domain.port.out.auth.SignupSessionProvider;
import ojik.ojikback.domain.port.out.auth.SocialAccountReader;
import ojik.ojikback.domain.port.out.auth.SocialAccountWriter;
import ojik.ojikback.domain.port.out.auth.TeamReader;
import ojik.ojikback.domain.port.out.auth.model.AuthTokens;
import ojik.ojikback.domain.port.out.auth.model.SignupSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SocialSignupServiceTest {
    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2026, 3, 9, 12, 0, 0);

    @Test
    @DisplayName("signupToken과 favoriteTeamId로 회원가입을 완료하고 JWT를 발급한다")
    void signupSuccess() {
        // given
        Team team = team(3L);
        SignupSession signupSession = signupSession("123456");

        SocialSignupService service = new SocialSignupService(
                new StubSignupSessionProvider(signupSession),
                (provider, providerUserId) -> Optional.empty(),
                nickname -> false,
                teamId -> Optional.of(team),
                member -> savedMember(member, 1L),
                socialAccount -> socialAccount,
                issuedMember -> new AuthTokens("jwt-access", "jwt-refresh")
        );

        // when
        SocialSignupResult result = service.signup(new SocialSignupCommand("signup-token", "노을", 3L));

        // then
        assertThat(result.accessToken()).isEqualTo("jwt-access");
        assertThat(result.refreshToken()).isEqualTo("jwt-refresh");
        assertThat(result.member()).isNotNull();
        assertThat(result.member().id()).isEqualTo(1L);
        assertThat(result.member().nickname()).isEqualTo("노을");
        assertThat(result.member().favoriteTeamId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("signupToken이 유효하지 않으면 INVALID_SIGNUP_TOKEN 예외를 던진다")
    void signupFailWhenTokenInvalid() {
        // given
        SocialSignupService service = new SocialSignupService(
                new InvalidSignupSessionProvider(),
                (provider, providerUserId) -> Optional.empty(),
                nickname -> false,
                teamId -> Optional.of(team(3L)),
                member -> member,
                socialAccount -> socialAccount,
                new UnusedAuthTokenProvider()
        );

        // when // then
        assertThatThrownBy(() -> service.signup(new SocialSignupCommand("bad-token", "노을", 3L)))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_SIGNUP_TOKEN);
    }

    @Test
    @DisplayName("이미 가입된 소셜 계정이면 DUPLICATE_SOCIAL_ACCOUNT 예외를 던진다")
    void signupFailWhenSocialAccountDuplicated() {
        // given
        SignupSession signupSession = signupSession("123456");
        SocialAccount socialAccount = SocialAccount.create(savedMember(Member.create("기존", team(1L), null, FIXED_DATE_TIME), 9L), SocialProvider.KAKAO, "123456", FIXED_DATE_TIME);

        SocialSignupService service = new SocialSignupService(
                new StubSignupSessionProvider(signupSession),
                (provider, providerUserId) -> Optional.of(socialAccount),
                nickname -> false,
                teamId -> Optional.of(team(3L)),
                member -> member,
                account -> account,
                new UnusedAuthTokenProvider()
        );

        // when // then
        assertThatThrownBy(() -> service.signup(new SocialSignupCommand("signup-token", "노을", 3L)))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.DUPLICATE_SOCIAL_ACCOUNT);
    }

    @Test
    @DisplayName("이미 사용 중인 닉네임이면 DUPLICATE_NICKNAME 예외를 던진다")
    void signupFailWhenNicknameDuplicated() {
        // given
        SocialSignupService service = new SocialSignupService(
                new StubSignupSessionProvider(signupSession("123456")),
                (provider, providerUserId) -> Optional.empty(),
                nickname -> true,
                teamId -> Optional.of(team(3L)),
                member -> member,
                socialAccount -> socialAccount,
                new UnusedAuthTokenProvider()
        );

        // when // then
        assertThatThrownBy(() -> service.signup(new SocialSignupCommand("signup-token", "노을", 3L)))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.DUPLICATE_NICKNAME);
    }

    @Test
    @DisplayName("favoriteTeamId에 해당하는 팀이 없으면 TEAM_NOT_FOUND 예외를 던진다")
    void signupFailWhenTeamNotFound() {
        // given
        SocialSignupService service = new SocialSignupService(
                new StubSignupSessionProvider(signupSession("123456")),
                (provider, providerUserId) -> Optional.empty(),
                nickname -> false,
                teamId -> Optional.empty(),
                member -> member,
                socialAccount -> socialAccount,
                new UnusedAuthTokenProvider()
        );

        // when // then
        assertThatThrownBy(() -> service.signup(new SocialSignupCommand("signup-token", "노을", 3L)))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.TEAM_NOT_FOUND);
    }

    private Team team(Long teamId) {
        Team team = Team.create("LG Twins");
        ReflectionTestUtils.setField(team, "id", teamId);
        return team;
    }

    private Member savedMember(Member member, Long memberId) {
        ReflectionTestUtils.setField(member, "id", memberId);
        return member;
    }

    private SignupSession signupSession(String providerUserId) {
        LocalDateTime now = FIXED_DATE_TIME;
        return new SignupSession(SocialProvider.KAKAO, providerUserId, now, now.plusMinutes(15));
    }

    private static final class StubSignupSessionProvider implements SignupSessionProvider {
        private final SignupSession signupSession;

        private StubSignupSessionProvider(SignupSession signupSession) {
            this.signupSession = signupSession;
        }

        @Override
        public String create(ojik.ojikback.domain.port.out.auth.model.SocialUserInfo userInfo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SignupSession get(String token) {
            return signupSession;
        }
    }

    private static final class InvalidSignupSessionProvider implements SignupSessionProvider {
        @Override
        public String create(ojik.ojikback.domain.port.out.auth.model.SocialUserInfo userInfo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SignupSession get(String token) {
            throw new AuthException(AuthErrorCode.INVALID_SIGNUP_TOKEN, "유효하지 않은 signupToken입니다.");
        }
    }

    private static final class UnusedAuthTokenProvider implements AuthTokenProvider {
        @Override
        public AuthTokens issue(Member member) {
            throw new UnsupportedOperationException();
        }
    }
}
