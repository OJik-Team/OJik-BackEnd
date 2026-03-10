package ojik.ojikback.domain.service.auth;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.SocialAccount;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.in.auth.SocialLoginUseCase;
import ojik.ojikback.domain.port.in.auth.command.SocialLoginCommand;
import ojik.ojikback.domain.port.in.auth.result.MemberSummary;
import ojik.ojikback.domain.port.in.auth.result.SocialLoginResult;
import ojik.ojikback.domain.port.out.auth.AuthTokenProvider;
import ojik.ojikback.domain.port.out.auth.SignupSessionProvider;
import ojik.ojikback.domain.port.out.auth.SocialAccountReader;
import ojik.ojikback.domain.port.out.auth.SocialAuthClient;
import ojik.ojikback.domain.port.out.auth.model.AuthTokens;
import ojik.ojikback.domain.port.out.auth.model.SocialUserInfo;

public class SocialLoginService implements SocialLoginUseCase {
    private final Map<SocialProvider, SocialAuthClient> socialAuthClients;
    private final SocialAccountReader socialAccountReader;
    private final SignupSessionProvider signupSessionProvider;
    private final AuthTokenProvider authTokenProvider;

    public SocialLoginService(
            Collection<SocialAuthClient> socialAuthClients,
            SocialAccountReader socialAccountReader,
            SignupSessionProvider signupSessionProvider,
            AuthTokenProvider authTokenProvider
    ) {
        this.socialAuthClients = toClientMap(socialAuthClients);
        this.socialAccountReader = socialAccountReader;
        this.signupSessionProvider = signupSessionProvider;
        this.authTokenProvider = authTokenProvider;
    }

    @Override
    public SocialLoginResult login(SocialLoginCommand command) {
        SocialAuthClient socialAuthClient = resolveClient(command.provider());
        SocialUserInfo userInfo = socialAuthClient.fetchUserInfo(command.authCode());

        return socialAccountReader.findByProviderAndProviderUserId(userInfo.provider(), userInfo.providerUserId())
                .map(this::toExistingMemberResult)
                .orElseGet(() -> SocialLoginResult.newMember(signupSessionProvider.create(userInfo)));
    }

    private SocialLoginResult toExistingMemberResult(SocialAccount socialAccount) {
        Member member = socialAccount.getMember();
        AuthTokens authTokens = authTokenProvider.issue(member);
        return SocialLoginResult.existingMember(
                authTokens.accessToken(),
                authTokens.refreshToken(),
                MemberSummary.from(member)
        );
    }

    private SocialAuthClient resolveClient(SocialProvider provider) {
        SocialAuthClient socialAuthClient = socialAuthClients.get(provider);
        if (socialAuthClient == null) {
            throw new AuthException(AuthErrorCode.INVALID_PROVIDER, "지원하지 않는 소셜 제공자입니다: " + provider);
        }
        return socialAuthClient;
    }

    private Map<SocialProvider, SocialAuthClient> toClientMap(Collection<SocialAuthClient> socialAuthClients) {
        Map<SocialProvider, SocialAuthClient> clientMap = new EnumMap<>(SocialProvider.class);
        for (SocialAuthClient socialAuthClient : socialAuthClients) {
            clientMap.put(socialAuthClient.provider(), socialAuthClient);
        }
        return clientMap;
    }
}
