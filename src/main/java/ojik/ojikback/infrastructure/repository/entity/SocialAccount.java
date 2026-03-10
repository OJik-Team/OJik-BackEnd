package ojik.ojikback.infrastructure.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ojik.ojikback.infrastructure.repository.entity.enums.SocialProvider;

@Entity
@Table(
        name = "social_accounts",
        uniqueConstraints = @UniqueConstraint(name = "uk_social_provider_user", columnNames = {"provider", "provider_user_id"})
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 50)
    private String providerUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static SocialAccount create(Member member, SocialProvider provider, String providerUserId, LocalDateTime createdAt) {
        SocialAccount socialAccount = new SocialAccount();
        socialAccount.member = member;
        socialAccount.provider = provider;
        socialAccount.providerUserId = providerUserId;
        socialAccount.createdAt = createdAt;
        return socialAccount;
    }
}
