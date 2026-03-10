package ojik.ojikback.domain.port.out.auth.model;

import java.time.LocalDateTime;
import ojik.ojikback.domain.entity.enums.SocialProvider;

public record SignupSession(
        SocialProvider provider,
        String providerUserId,
        LocalDateTime issuedAt,
        LocalDateTime expiresAt
) {
}
