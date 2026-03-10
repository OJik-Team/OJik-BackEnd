package ojik.ojikback.api.adapter.in.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.in.auth.SocialLoginUseCase;
import ojik.ojikback.domain.port.in.auth.SocialSignupUseCase;
import ojik.ojikback.domain.port.in.auth.command.SocialLoginCommand;
import ojik.ojikback.domain.port.in.auth.command.SocialSignupCommand;
import ojik.ojikback.domain.port.in.auth.result.MemberSummary;
import ojik.ojikback.domain.port.in.auth.result.SocialLoginResult;
import ojik.ojikback.domain.port.in.auth.result.SocialSignupResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SocialAuthControllerTest {
    @Test
    @DisplayName("소셜 로그인 API는 기존 회원 응답을 반환한다")
    void loginSuccess() throws Exception {
        // given
        SocialAuthController controller = new SocialAuthController(
                new StubSocialLoginUseCase(SocialLoginResult.existingMember(
                        "jwt-access",
                        "jwt-refresh",
                        new MemberSummary(1L, "다영", 3L)
                )),
                command -> {
                    throw new UnsupportedOperationException();
                }
        );
        MockMvc mockMvc = mockMvc(controller);

        // when // then
        mockMvc.perform(post("/api/auth/social/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "KAKAO",
                                  "authCode": "auth-code"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isNew").value(false))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-access"))
                .andExpect(jsonPath("$.data.member.nickname").value("다영"));
    }

    @Test
    @DisplayName("소셜 회원가입 API는 회원가입 완료 응답을 반환한다")
    void signupSuccess() throws Exception {
        // given
        SocialAuthController controller = new SocialAuthController(
                command -> {
                    throw new UnsupportedOperationException();
                },
                new StubSocialSignupUseCase(new SocialSignupResult(
                        "jwt-access",
                        "jwt-refresh",
                        new MemberSummary(1L, "노을", 3L)
                ))
        );
        MockMvc mockMvc = mockMvc(controller);

        // when // then
        mockMvc.perform(post("/api/auth/social/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "signupToken": "signup-token",
                                  "nickname": "노을",
                                  "favoriteTeamId": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isNew").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-access"))
                .andExpect(jsonPath("$.data.member.favoriteTeamId").value(3));
    }

    @Test
    @DisplayName("인증 예외는 에러코드에 맞는 HTTP 상태로 변환한다")
    void handleAuthException() throws Exception {
        // given
        SocialAuthController controller = new SocialAuthController(
                command -> {
                    throw new AuthException(AuthErrorCode.INVALID_PROVIDER, "지원하지 않는 소셜 제공자입니다.");
                },
                command -> {
                    throw new UnsupportedOperationException();
                }
        );
        MockMvc mockMvc = mockMvc(controller);

        // when // then
        mockMvc.perform(post("/api/auth/social/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "unknown",
                                  "authCode": "auth-code"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_PROVIDER"));
    }

    @Test
    @DisplayName("회원가입 중복 예외는 409와 공통 에러 스키마로 응답한다")
    void handleConflictException() throws Exception {
        // given
        SocialAuthController controller = new SocialAuthController(
                command -> {
                    throw new UnsupportedOperationException();
                },
                command -> {
                    throw new AuthException(AuthErrorCode.DUPLICATE_NICKNAME, "이미 사용 중인 닉네임입니다.");
                }
        );
        MockMvc mockMvc = mockMvc(controller);

        // when // then
        mockMvc.perform(post("/api/auth/social/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "signupToken": "signup-token",
                                  "nickname": "노을",
                                  "favoriteTeamId": 3
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value("DUPLICATE_NICKNAME"));
    }

    @Test
    @DisplayName("필수 요청값이 없으면 400과 공통 에러 스키마로 응답한다")
    void handleInvalidRequest() throws Exception {
        // given
        SocialAuthController controller = new SocialAuthController(
                command -> {
                    throw new UnsupportedOperationException();
                },
                command -> {
                    throw new UnsupportedOperationException();
                }
        );
        MockMvc mockMvc = mockMvc(controller);

        // when // then
        mockMvc.perform(post("/api/auth/social/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "signupToken": "",
                                  "nickname": "노을"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("잘못된 JSON 타입이면 400과 공통 에러 스키마로 응답한다")
    void handleMalformedJson() throws Exception {
        // given
        SocialAuthController controller = new SocialAuthController(
                command -> {
                    throw new UnsupportedOperationException();
                },
                command -> {
                    throw new UnsupportedOperationException();
                }
        );
        MockMvc mockMvc = mockMvc(controller);

        // when // then
        mockMvc.perform(post("/api/auth/social/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "signupToken": "signup-token",
                                  "nickname": "노을",
                                  "favoriteTeamId": "abc"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    private MockMvc mockMvc(SocialAuthController controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new AuthExceptionHandler())
                .build();
    }

    private record StubSocialLoginUseCase(SocialLoginResult result) implements SocialLoginUseCase {
        @Override
        public SocialLoginResult login(SocialLoginCommand command) {
            return result;
        }
    }

    private record StubSocialSignupUseCase(SocialSignupResult result) implements SocialSignupUseCase {
        @Override
        public SocialSignupResult signup(SocialSignupCommand command) {
            return result;
        }
    }
}
