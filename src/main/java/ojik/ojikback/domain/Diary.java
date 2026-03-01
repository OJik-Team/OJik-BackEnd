package ojik.ojikback.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diary")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    @Column(name = "game_date", nullable = false)
    private LocalDate gameDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_team_id", nullable = false)
    private Team supportTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_team_id", nullable = false)
    private Team opponentTeam;

    @Column(name = "stadium_name", nullable = false, length = 100)
    private String stadiumName;

    @Column(name = "seat_section", length = 30)
    private String seatSection;

    @Column(name = "seat_row", length = 30)
    private String seatRow;

    @Column(name = "seat_number", length = 30)
    private String seatNumber;

    @Lob
    @Column(name = "diary_content")
    private String diaryContent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
