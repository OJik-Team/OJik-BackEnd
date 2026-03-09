package ojik.ojikback.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Member {
    private Long id;
    private String nickname;
    private Team favoriteTeam;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private final List<SocialAccount> socialAccounts = new ArrayList<>();

    public static Member create(String nickname, Team favoriteTeam, String profileImageUrl, LocalDateTime now) {
        Member member = new Member();
        member.nickname = nickname;
        member.favoriteTeam = favoriteTeam;
        member.profileImageUrl = profileImageUrl;
        member.createdAt = now;
        member.updatedAt = now;
        return member;
    }

    public static Member restore(
            Long id,
            String nickname,
            Team favoriteTeam,
            String profileImageUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        Member member = new Member();
        member.id = id;
        member.nickname = nickname;
        member.favoriteTeam = favoriteTeam;
        member.profileImageUrl = profileImageUrl;
        member.createdAt = createdAt;
        member.updatedAt = updatedAt;
        member.deletedAt = deletedAt;
        return member;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Team getFavoriteTeam() {
        return favoriteTeam;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public List<SocialAccount> getSocialAccounts() {
        return socialAccounts;
    }

    public void linkSocialAccount(SocialAccount socialAccount) {
        socialAccounts.add(socialAccount);
    }
}
