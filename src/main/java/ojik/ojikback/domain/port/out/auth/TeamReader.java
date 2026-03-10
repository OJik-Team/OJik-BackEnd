package ojik.ojikback.domain.port.out.auth;

import java.util.Optional;
import ojik.ojikback.domain.entity.Team;

public interface TeamReader {
    Optional<Team> findById(Long teamId);
}
