package ojik.ojikback.domain.port.in.auth;

import ojik.ojikback.domain.port.in.auth.command.SocialSignupCommand;
import ojik.ojikback.domain.port.in.auth.result.SocialSignupResult;

public interface SocialSignupUseCase {
    SocialSignupResult signup(SocialSignupCommand command);
}
