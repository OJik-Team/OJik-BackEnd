package ojik.ojikback.domain.entity;

import java.time.LocalDateTime;
import ojik.ojikback.domain.entity.enums.SocialProvider;

public class SocialAccount {
    private Long id;
    private Member member;
    private SocialProvider provider;
    private String providerUserId;
    private LocalDateTime createdAt;

    public static SocialAccount create(Member member, SocialProvider provider, String providerUserId, LocalDateTime now) {
        SocialAccount socialAccount = new SocialAccount();
        socialAccount.member = member;
        socialAccount.provider = provider;
        socialAccount.providerUserId = providerUserId;
        socialAccount.createdAt = now;
        return socialAccount;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public SocialProvider getProvider() {
        return provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
