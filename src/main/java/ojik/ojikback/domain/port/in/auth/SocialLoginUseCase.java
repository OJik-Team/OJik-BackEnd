package ojik.ojikback.domain.port.in.auth;

import ojik.ojikback.domain.port.in.auth.command.SocialLoginCommand;
import ojik.ojikback.domain.port.in.auth.result.SocialLoginResult;

public interface SocialLoginUseCase {
    SocialLoginResult login(SocialLoginCommand command);
}
