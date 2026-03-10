package ojik.ojikback.infrastructure.repository;

import ojik.ojikback.infrastructure.repository.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTeamRepository extends JpaRepository<Team, Long> {
}
