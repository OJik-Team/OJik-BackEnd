package ojik.ojikback.infrastructure.adapter.in.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Map;
import ojik.ojikback.OjikbackApplication;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.Team;
import ojik.ojikback.domain.port.out.auth.AuthTokenProvider;
import ojik.ojikback.domain.port.out.auth.model.AuthTokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = OjikbackApplication.class)
@ActiveProfiles("test")
@Import(SecurityConfigTest.TestProtectedApiConfiguration.class)
class SecurityConfigTest {

    private MockMvc mockMvc;

    @Autowired
    private AuthTokenProvider authTokenProvider;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("보호 API는 access token 없이 호출하면 401을 반환한다")
    void rejectRequestWithoutAccessToken() throws Exception {
        // given // when // then
        mockMvc.perform(get("/api/test/protected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("헬스 체크 API는 access token 없이 호출해도 통과한다")
    void allowHealthCheckWithoutAccessToken() throws Exception {
        // given // when // then
        mockMvc.perform(get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    @DisplayName("소셜 로그인 API는 잘못된 Authorization 헤더가 있어도 공개 경로로 동작한다")
    void allowPublicAuthEndpointEvenWithInvalidAuthorizationHeader() throws Exception {
        // given // when // then
        mockMvc.perform(post("/api/auth/social/login")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "",
                                  "authCode": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("보호 API는 refresh token으로 호출하면 401을 반환한다")
    void rejectRequestWithRefreshToken() throws Exception {
        // given
        AuthTokens authTokens = authTokenProvider.issue(member(1L, "노을"));

        // when // then
        mockMvc.perform(get("/api/test/protected")
                        .header("Authorization", "Bearer " + authTokens.refreshToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_ACCESS_TOKEN"));
    }

    @Test
    @DisplayName("보호 API는 유효한 access token으로 호출하면 통과한다")
    void allowRequestWithAccessToken() throws Exception {
        // given
        AuthTokens authTokens = authTokenProvider.issue(member(1L, "노을"));

        // when // then
        mockMvc.perform(get("/api/test/protected")
                        .header("Authorization", "Bearer " + authTokens.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.nickname").value("노을"));
    }

    private Member member(Long memberId, String nickname) {
        LocalDateTime now = LocalDateTime.now();
        return Member.restore(
                memberId,
                nickname,
                Team.restore(1L, "LG Twins"),
                null,
                now,
                now,
                null
        );
    }

    @TestConfiguration
    static class TestProtectedApiConfiguration {

        @Bean
        TestProtectedApiController testProtectedApiController() {
            return new TestProtectedApiController();
        }
    }

    @RestController
    static class TestProtectedApiController {
        @GetMapping("/api/test/protected")
        Map<String, Object> protectedApi(Authentication authentication) {
            AuthenticatedMember authenticatedMember = (AuthenticatedMember) authentication.getPrincipal();
            return Map.of(
                    "memberId", authenticatedMember.memberId(),
                    "nickname", authenticatedMember.nickname()
            );
        }
    }
}
