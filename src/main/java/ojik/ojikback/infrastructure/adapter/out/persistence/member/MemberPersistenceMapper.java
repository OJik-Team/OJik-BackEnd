package ojik.ojikback.infrastructure.adapter.out.persistence.member;

import org.springframework.stereotype.Component;

@Component
public class MemberPersistenceMapper {

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

    private ojik.ojikback.domain.entity.Team toDomainTeam(ojik.ojikback.infrastructure.repository.entity.Team entity) {
        return ojik.ojikback.domain.entity.Team.restore(
                entity.getId(),
                entity.getName()
        );
    }
}
