package ojik.ojikback.domain.entity;

import java.time.LocalDate;
import ojik.ojikback.domain.entity.enums.GameStatus;

public class Game {
    private Long id;
    private LocalDate gameDate;
    private Team homeTeam;
    private Team awayTeam;
    private Stadium stadium;
    private Integer homeScore;
    private Integer awayScore;
    private GameStatus status;

    public Long getId() {
        return id;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public GameStatus getStatus() {
        return status;
    }
}
