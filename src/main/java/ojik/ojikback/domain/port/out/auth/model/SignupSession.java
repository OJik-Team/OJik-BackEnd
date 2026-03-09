package ojik.ojikback.domain.port.out.auth.model;

import java.time.Instant;
import ojik.ojikback.domain.entity.enums.SocialProvider;

public record SignupSession(
        SocialProvider provider,
        String providerUserId,
        Instant issuedAt,
        Instant expiresAt
) {
}
