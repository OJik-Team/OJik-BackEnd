package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.Stadium;

public interface StadiumRepository {
    Optional<Stadium> findById(Long stadiumId);
}
