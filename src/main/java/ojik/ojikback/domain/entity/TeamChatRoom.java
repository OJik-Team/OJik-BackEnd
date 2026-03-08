package ojik.ojikback.domain.entity;

import java.time.LocalDateTime;

public class TeamChatRoom {
    private Long id;
    private Team team;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
