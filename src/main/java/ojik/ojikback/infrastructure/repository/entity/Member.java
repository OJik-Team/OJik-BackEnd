package ojik.ojikback.infrastructure.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_team_id", nullable = false)
    private Team favoriteTeam;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member")
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    public static Member create(
            String nickname,
            Team favoriteTeam,
            String profileImageUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        Member member = new Member();
        member.nickname = nickname;
        member.favoriteTeam = favoriteTeam;
        member.profileImageUrl = profileImageUrl;
        member.createdAt = createdAt;
        member.updatedAt = updatedAt;
        member.deletedAt = deletedAt;
        return member;
    }
}
