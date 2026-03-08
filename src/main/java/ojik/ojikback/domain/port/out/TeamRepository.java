package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.Team;

public interface TeamRepository {
    Optional<Team> findById(Long teamId);
}
