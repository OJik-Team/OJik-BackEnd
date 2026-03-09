package ojik.ojikback.domain.port.out.auth;

import java.util.Optional;
import ojik.ojikback.domain.entity.SocialAccount;
import ojik.ojikback.domain.entity.enums.SocialProvider;

public interface SocialAccountReader {
    Optional<SocialAccount> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);
}
