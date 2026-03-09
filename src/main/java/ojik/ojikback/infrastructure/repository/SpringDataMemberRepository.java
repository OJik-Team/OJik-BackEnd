package ojik.ojikback.infrastructure.repository;

import ojik.ojikback.infrastructure.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickname(String nickname);
}
