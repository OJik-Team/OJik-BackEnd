package ojik.ojikback.api.adapter.in.auth;

import java.util.Locale;
import ojik.ojikback.api.adapter.in.auth.dto.SocialLoginRequest;
import ojik.ojikback.api.adapter.in.auth.dto.SocialSignupRequest;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.in.auth.command.SocialLoginCommand;
import ojik.ojikback.domain.port.in.auth.command.SocialSignupCommand;

public final class SocialAuthRequestMapper {

    private SocialAuthRequestMapper() {
    }

    public static SocialLoginCommand toCommand(SocialLoginRequest request) {
        return new SocialLoginCommand(parseProvider(request.provider()), request.authCode());
    }

    public static SocialSignupCommand toCommand(SocialSignupRequest request) {
        return new SocialSignupCommand(request.signupToken(), request.nickname(), request.favoriteTeamId());
    }

    private static SocialProvider parseProvider(String provider) {
        try {
            return SocialProvider.valueOf(provider.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.INVALID_PROVIDER, "지원하지 않는 소셜 제공자입니다: " + provider);
        }
    }
}
