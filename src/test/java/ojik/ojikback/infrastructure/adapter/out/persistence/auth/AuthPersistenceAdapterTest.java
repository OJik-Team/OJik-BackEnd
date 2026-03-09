package ojik.ojikback.infrastructure.adapter.out.persistence.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import ojik.ojikback.OjikbackApplication;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.SocialAccount;
import ojik.ojikback.domain.entity.Team;
import ojik.ojikback.domain.entity.enums.SocialProvider;
import ojik.ojikback.infrastructure.repository.SpringDataMemberRepository;
import ojik.ojikback.infrastructure.repository.SpringDataSocialAccountRepository;
import ojik.ojikback.infrastructure.repository.SpringDataTeamRepository;
import org.springframework.beans.BeanUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = OjikbackApplication.class)
@ActiveProfiles("test")
class AuthPersistenceAdapterTest {

    @Autowired
    private AuthPersistenceAdapter authPersistenceAdapter;

    @Autowired
    private SpringDataTeamRepository teamRepository;

    @Autowired
    private SpringDataMemberRepository memberRepository;

    @Autowired
    private SpringDataSocialAccountRepository socialAccountRepository;

    @Test
    @DisplayName("회원 저장 결과를 도메인으로 매핑할 때 favoriteTeam lazy 프록시로 실패하지 않는다")
    void saveMemberWithoutLazyInitializationFailure() {
        // given
        ojik.ojikback.infrastructure.repository.entity.Team teamEntity = teamEntity("LG Twins");
        ojik.ojikback.infrastructure.repository.entity.Team savedTeam = teamRepository.save(teamEntity);

        Member member = Member.create(
                "노을",
                Team.restore(savedTeam.getId(), savedTeam.getName()),
                null,
                LocalDateTime.now()
        );

        // when
        Member savedMember = authPersistenceAdapter.save(member);

        // then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getFavoriteTeam()).isNotNull();
        assertThat(savedMember.getFavoriteTeam().getId()).isEqualTo(savedTeam.getId());
        assertThat(savedMember.getFavoriteTeam().getName()).isEqualTo("LG Twins");
    }

    @Test
    @DisplayName("소셜 계정 저장 결과를 도메인으로 매핑할 때 member와 favoriteTeam을 안전하게 복원한다")
    void saveSocialAccountWithoutLazyInitializationFailure() {
        // given
        ojik.ojikback.infrastructure.repository.entity.Team teamEntity = teamEntity("LG Twins");
        ojik.ojikback.infrastructure.repository.entity.Team savedTeam = teamRepository.save(teamEntity);

        ojik.ojikback.infrastructure.repository.entity.Member memberEntity =
                ojik.ojikback.infrastructure.repository.entity.Member.create(
                        "노을",
                        savedTeam,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        null
                );
        ojik.ojikback.infrastructure.repository.entity.Member savedMemberEntity = memberRepository.save(memberEntity);

        SocialAccount socialAccount = SocialAccount.create(
                Member.restore(
                        savedMemberEntity.getId(),
                        savedMemberEntity.getNickname(),
                        Team.restore(savedTeam.getId(), savedTeam.getName()),
                        savedMemberEntity.getProfileImageUrl(),
                        savedMemberEntity.getCreatedAt(),
                        savedMemberEntity.getUpdatedAt(),
                        savedMemberEntity.getDeletedAt()
                ),
                SocialProvider.KAKAO,
                "provider-user-id",
                LocalDateTime.now()
        );

        // when
        SocialAccount savedSocialAccount = authPersistenceAdapter.save(socialAccount);

        // then
        assertThat(savedSocialAccount.getId()).isNotNull();
        assertThat(savedSocialAccount.getMember().getId()).isEqualTo(savedMemberEntity.getId());
        assertThat(savedSocialAccount.getMember().getFavoriteTeam().getName()).isEqualTo("LG Twins");
        assertThat(socialAccountRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 팀으로 회원 저장을 시도하면 예외를 던진다")
    void saveMemberFailWhenFavoriteTeamMissing() {
        // given
        Member member = Member.create(
                "노을",
                Team.restore(999L, "Unknown Team"),
                null,
                LocalDateTime.now()
        );

        // when // then
        assertThatThrownBy(() -> authPersistenceAdapter.save(member))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("존재하지 않는 팀입니다");
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 소셜 계정 저장을 시도하면 예외를 던진다")
    void saveSocialAccountFailWhenMemberMissing() {
        // given
        SocialAccount socialAccount = SocialAccount.create(
                Member.restore(
                        999L,
                        "노을",
                        Team.restore(1L, "LG Twins"),
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        null
                ),
                SocialProvider.KAKAO,
                "provider-user-id",
                LocalDateTime.now()
        );

        // when // then
        assertThatThrownBy(() -> authPersistenceAdapter.save(socialAccount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("존재하지 않는 회원입니다");
    }

    private ojik.ojikback.infrastructure.repository.entity.Team teamEntity(String name) {
        ojik.ojikback.infrastructure.repository.entity.Team team =
                BeanUtils.instantiateClass(ojik.ojikback.infrastructure.repository.entity.Team.class);
        team.setName(name);
        return team;
    }
}
