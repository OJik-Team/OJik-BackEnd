package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.SocialAccount;
import ojik.ojikback.domain.entity.enums.SocialProvider;

public interface SocialAccountRepository {
    SocialAccount save(SocialAccount socialAccount);

    Optional<SocialAccount> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);
}
