package ojik.ojikback.api.adapter.in.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import ojik.ojikback.OjikbackApplication;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.Team;
import ojik.ojikback.domain.port.out.auth.AuthTokenProvider;
import ojik.ojikback.domain.port.out.auth.model.AuthTokens;
import ojik.ojikback.infrastructure.repository.SpringDataMemberRepository;
import ojik.ojikback.infrastructure.repository.SpringDataSocialAccountRepository;
import ojik.ojikback.infrastructure.repository.SpringDataTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = OjikbackApplication.class)
@ActiveProfiles("test")
class MemberWithdrawalApiIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SpringDataTeamRepository teamRepository;

    @Autowired
    private SpringDataMemberRepository memberRepository;

    @Autowired
    private SpringDataSocialAccountRepository socialAccountRepository;

    @Autowired
    private AuthTokenProvider authTokenProvider;

    @BeforeEach
    void cleanUp() {
        socialAccountRepository.deleteAll();
        memberRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 탈퇴 API는 회원을 soft delete하고 소셜 계정을 삭제한다")
    void withdrawSuccess() throws Exception {
        // given
        MockMvc mockMvc = mockMvc();
        ojik.ojikback.infrastructure.repository.entity.Team savedTeam = teamRepository.save(teamEntity("LG Twins"));
        ojik.ojikback.infrastructure.repository.entity.Member savedMember = memberRepository.save(
                ojik.ojikback.infrastructure.repository.entity.Member.create(
                        "노을",
                        savedTeam,
                        null,
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().minusDays(1),
                        null
                )
        );
        socialAccountRepository.save(
                ojik.ojikback.infrastructure.repository.entity.SocialAccount.create(
                        savedMember,
                        ojik.ojikback.infrastructure.repository.entity.enums.SocialProvider.KAKAO,
                        "provider-user-id",
                        LocalDateTime.now().minusDays(1)
                )
        );
        AuthTokens authTokens = authTokenProvider.issue(Member.restore(
                savedMember.getId(),
                savedMember.getNickname(),
                Team.restore(savedTeam.getId(), savedTeam.getName()),
                savedMember.getProfileImageUrl(),
                savedMember.getCreatedAt(),
                savedMember.getUpdatedAt(),
                savedMember.getDeletedAt()
        ));

        // when
        mockMvc.perform(delete("/api/members/me")
                        .header("Authorization", "Bearer " + authTokens.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());

        // then
        org.assertj.core.api.Assertions.assertThat(memberRepository.findById(savedMember.getId()))
                .get()
                .extracting(ojik.ojikback.infrastructure.repository.entity.Member::getDeletedAt)
                .isNotNull();
        org.assertj.core.api.Assertions.assertThat(socialAccountRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 회원 access token으로 탈퇴 요청하면 404를 반환한다")
    void withdrawFailWhenMemberMissing() throws Exception {
        // given
        MockMvc mockMvc = mockMvc();
        AuthTokens authTokens = authTokenProvider.issue(Member.restore(
                999L,
                "없는회원",
                Team.restore(1L, "LG Twins"),
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        ));

        // when
        mockMvc.perform(delete("/api/members/me")
                        .header("Authorization", "Bearer " + authTokens.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("MEMBER_NOT_FOUND"));

        // then
    }

    private ojik.ojikback.infrastructure.repository.entity.Team teamEntity(String name) {
        ojik.ojikback.infrastructure.repository.entity.Team team =
                BeanUtils.instantiateClass(ojik.ojikback.infrastructure.repository.entity.Team.class);
        team.setName(name);
        return team;
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }
}
