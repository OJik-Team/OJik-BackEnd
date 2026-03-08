package ojik.ojikback.domain.entity;

import java.time.LocalDateTime;
import ojik.ojikback.domain.entity.enums.AttendanceStatus;

public class Attendance {
    private Long id;
    private Member member;
    private Game game;
    private AttendanceStatus status;
    private boolean hasDiary;
    private LocalDateTime certifiedAt;

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Game getGame() {
        return game;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public boolean isHasDiary() {
        return hasDiary;
    }

    public LocalDateTime getCertifiedAt() {
        return certifiedAt;
    }
}
