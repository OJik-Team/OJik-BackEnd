package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.Attendance;

public interface AttendanceRepository {
    Attendance save(Attendance attendance);

    Optional<Attendance> findById(Long attendanceId);
}
