package ojik.ojikback.infrastructure.adapter.out.persistence.auth;

import org.springframework.stereotype.Component;

@Component
public class AuthPersistenceMapper {

    public ojik.ojikback.domain.entity.Team toDomainTeam(ojik.ojikback.infrastructure.repository.entity.Team entity) {
        return ojik.ojikback.domain.entity.Team.restore(
                entity.getId(),
                entity.getName(),
                entity.getShortName(),
                entity.getThemeColor()
        );
    }

    public ojik.ojikback.domain.entity.Member toDomainMember(ojik.ojikback.infrastructure.repository.entity.Member entity) {
        return ojik.ojikback.domain.entity.Member.restore(
                entity.getId(),
                entity.getNickname(),
                toDomainTeam(entity.getFavoriteTeam()),
                entity.getProfileImageUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public ojik.ojikback.domain.entity.SocialAccount toDomainSocialAccount(ojik.ojikback.infrastructure.repository.entity.SocialAccount entity) {
        return ojik.ojikback.domain.entity.SocialAccount.restore(
                entity.getId(),
                toDomainMember(entity.getMember()),
                ojik.ojikback.domain.entity.enums.SocialProvider.valueOf(entity.getProvider().name()),
                entity.getProviderUserId(),
                entity.getCreatedAt()
        );
    }

    public ojik.ojikback.infrastructure.repository.entity.Member toEntityMember(
            ojik.ojikback.domain.entity.Member domain,
            ojik.ojikback.infrastructure.repository.entity.Team favoriteTeam
    ) {
        return ojik.ojikback.infrastructure.repository.entity.Member.create(
                domain.getNickname(),
                favoriteTeam,
                domain.getProfileImageUrl(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getDeletedAt()
        );
    }

    public ojik.ojikback.infrastructure.repository.entity.SocialAccount toEntitySocialAccount(
            ojik.ojikback.domain.entity.SocialAccount domain,
            ojik.ojikback.infrastructure.repository.entity.Member member
    ) {
        return ojik.ojikback.infrastructure.repository.entity.SocialAccount.create(
                member,
                ojik.ojikback.infrastructure.repository.entity.enums.SocialProvider.valueOf(domain.getProvider().name()),
                domain.getProviderUserId(),
                domain.getCreatedAt()
        );
    }
}
