package ojik.ojikback.infrastructure.repository;

import java.util.Optional;
import ojik.ojikback.infrastructure.repository.entity.SocialAccount;
import ojik.ojikback.infrastructure.repository.entity.enums.SocialProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    @EntityGraph(attributePaths = {"member", "member.favoriteTeam"})
    Optional<SocialAccount> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);
}
