package ojik.ojikback.domain.port.in.auth.command;

import ojik.ojikback.domain.entity.enums.SocialProvider;

public record SocialLoginCommand(
        SocialProvider provider,
        String authCode
) {
}
