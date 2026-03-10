package ojik.ojikback.infrastructure.adapter.in.config;

import java.util.List;
import ojik.ojikback.domain.port.in.auth.SocialLoginUseCase;
import ojik.ojikback.domain.port.in.auth.SocialSignupUseCase;
import ojik.ojikback.domain.port.out.auth.AuthTokenProvider;
import ojik.ojikback.domain.port.out.auth.MemberNicknameChecker;
import ojik.ojikback.domain.port.out.auth.MemberWriter;
import ojik.ojikback.domain.port.out.auth.SignupSessionProvider;
import ojik.ojikback.domain.port.out.auth.SocialAccountReader;
import ojik.ojikback.domain.port.out.auth.SocialAccountWriter;
import ojik.ojikback.domain.port.out.auth.SocialAuthClient;
import ojik.ojikback.domain.port.out.auth.TeamReader;
import ojik.ojikback.domain.service.auth.SocialLoginService;
import ojik.ojikback.domain.service.auth.SocialSignupService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public SocialLoginUseCase socialLoginUseCase(
            List<SocialAuthClient> socialAuthClients,
            SocialAccountReader socialAccountReader,
            SignupSessionProvider signupSessionProvider,
            AuthTokenProvider authTokenProvider
    ) {
        return new SocialLoginService(
                socialAuthClients,
                socialAccountReader,
                signupSessionProvider,
                authTokenProvider
        );
    }

    @Bean
    public SocialSignupUseCase socialSignupUseCase(
            SignupSessionProvider signupSessionProvider,
            SocialAccountReader socialAccountReader,
            MemberNicknameChecker memberNicknameChecker,
            TeamReader teamReader,
            MemberWriter memberWriter,
            SocialAccountWriter socialAccountWriter,
            AuthTokenProvider authTokenProvider
    ) {
        return new SocialSignupService(
                signupSessionProvider,
                socialAccountReader,
                memberNicknameChecker,
                teamReader,
                memberWriter,
                socialAccountWriter,
                authTokenProvider
        );
    }
}
