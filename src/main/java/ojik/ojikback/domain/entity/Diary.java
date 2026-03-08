package ojik.ojikback.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Diary {
    private Long id;
    private Member member;
    private Game game;
    private Attendance attendance;
    private LocalDate gameDate;
    private Team supportTeam;
    private Team opponentTeam;
    private String stadiumName;
    private String seatSection;
    private String seatRow;
    private String seatNumber;
    private String diaryContent;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Game getGame() {
        return game;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public Team getSupportTeam() {
        return supportTeam;
    }

    public Team getOpponentTeam() {
        return opponentTeam;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public String getSeatSection() {
        return seatSection;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getDiaryContent() {
        return diaryContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
