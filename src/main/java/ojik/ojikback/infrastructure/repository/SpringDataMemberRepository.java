package ojik.ojikback.infrastructure.repository;

import java.util.Optional;
import ojik.ojikback.infrastructure.repository.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNicknameAndDeletedAtIsNull(String nickname);

    @EntityGraph(attributePaths = {"favoriteTeam"})
    Optional<Member> findByIdAndDeletedAtIsNull(Long id);
}
