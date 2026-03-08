package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.Game;

public interface GameRepository {
    Game save(Game game);

    Optional<Game> findById(Long gameId);
}
