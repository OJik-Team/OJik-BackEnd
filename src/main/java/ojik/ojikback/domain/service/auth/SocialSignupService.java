package ojik.ojikback.domain.service.auth;

import java.time.LocalDateTime;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.SocialAccount;
import ojik.ojikback.domain.entity.Team;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.in.auth.SocialSignupUseCase;
import ojik.ojikback.domain.port.in.auth.command.SocialSignupCommand;
import ojik.ojikback.domain.port.in.auth.result.MemberSummary;
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

public class SocialSignupService implements SocialSignupUseCase {
    private final SignupSessionProvider signupSessionProvider;
    private final SocialAccountReader socialAccountReader;
    private final MemberNicknameChecker memberNicknameChecker;
    private final TeamReader teamReader;
    private final MemberWriter memberWriter;
    private final SocialAccountWriter socialAccountWriter;
    private final AuthTokenProvider authTokenProvider;

    public SocialSignupService(
            SignupSessionProvider signupSessionProvider,
            SocialAccountReader socialAccountReader,
            MemberNicknameChecker memberNicknameChecker,
            TeamReader teamReader,
            MemberWriter memberWriter,
            SocialAccountWriter socialAccountWriter,
            AuthTokenProvider authTokenProvider
    ) {
        this.signupSessionProvider = signupSessionProvider;
        this.socialAccountReader = socialAccountReader;
        this.memberNicknameChecker = memberNicknameChecker;
        this.teamReader = teamReader;
        this.memberWriter = memberWriter;
        this.socialAccountWriter = socialAccountWriter;
        this.authTokenProvider = authTokenProvider;
    }

    @Override
    public SocialSignupResult signup(SocialSignupCommand command) {
        SignupSession signupSession = signupSessionProvider.get(command.signupToken());

        validateDuplicateSocialAccount(signupSession);
        validateDuplicateNickname(command.nickname());

        Team favoriteTeam = teamReader.findById(command.favoriteTeamId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.TEAM_NOT_FOUND, "존재하지 않는 팀입니다: " + command.favoriteTeamId()));

        LocalDateTime now = LocalDateTime.now();
        Member savedMember = memberWriter.save(Member.create(command.nickname(), favoriteTeam, null, now));

        SocialAccount savedSocialAccount = socialAccountWriter.save(
                SocialAccount.create(savedMember, signupSession.provider(), signupSession.providerUserId(), now)
        );
        savedMember.linkSocialAccount(savedSocialAccount);

        AuthTokens authTokens = authTokenProvider.issue(savedMember);
        return new SocialSignupResult(
                authTokens.accessToken(),
                authTokens.refreshToken(),
                MemberSummary.from(savedMember)
        );
    }

    private void validateDuplicateSocialAccount(SignupSession signupSession) {
        boolean duplicate = socialAccountReader.findByProviderAndProviderUserId(
                signupSession.provider(),
                signupSession.providerUserId()
        ).isPresent();

        if (duplicate) {
            throw new AuthException(AuthErrorCode.DUPLICATE_SOCIAL_ACCOUNT, "이미 가입된 소셜 계정입니다.");
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (memberNicknameChecker.existsByNickname(nickname)) {
            throw new AuthException(AuthErrorCode.DUPLICATE_NICKNAME, "이미 사용 중인 닉네임입니다: " + nickname);
        }
    }
}
