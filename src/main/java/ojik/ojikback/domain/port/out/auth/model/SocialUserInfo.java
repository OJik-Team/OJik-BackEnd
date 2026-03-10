package ojik.ojikback.domain.port.out.auth.model;

import ojik.ojikback.domain.entity.enums.SocialProvider;

public record SocialUserInfo(
        SocialProvider provider,
        String providerUserId
) {
}
