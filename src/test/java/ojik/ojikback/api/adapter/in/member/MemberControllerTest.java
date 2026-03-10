package ojik.ojikback.api.adapter.in.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ojik.ojikback.api.adapter.in.auth.AuthExceptionHandler;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.in.member.WithdrawMemberUseCase;
import ojik.ojikback.domain.port.in.member.command.WithdrawMemberCommand;
import ojik.ojikback.infrastructure.adapter.in.security.AuthenticatedMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MemberControllerTest {

    @Test
    @DisplayName("회원 탈퇴 API는 인증된 회원 id로 탈퇴를 요청한다")
    void withdrawSuccess() throws Exception {
        // given
        CapturingWithdrawMemberUseCase useCase = new CapturingWithdrawMemberUseCase();
        MemberController controller = new MemberController(useCase);
        MockMvc mockMvc = mockMvc(controller);

        // when
        mockMvc.perform(delete("/api/members/me")
                        .principal(new UsernamePasswordAuthenticationToken(new AuthenticatedMember(1L, "노을"), null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());

        // then
        org.assertj.core.api.Assertions.assertThat(useCase.memberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("탈퇴 대상 회원이 없으면 404를 반환한다")
    void withdrawFailWhenMemberMissing() throws Exception {
        // given
        MemberController controller = new MemberController(command -> {
            throw new AuthException(AuthErrorCode.MEMBER_NOT_FOUND, "존재하지 않는 회원입니다.");
        });
        MockMvc mockMvc = mockMvc(controller);

        // when
        mockMvc.perform(delete("/api/members/me")
                        .principal(new UsernamePasswordAuthenticationToken(new AuthenticatedMember(1L, "노을"), null)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("MEMBER_NOT_FOUND"));

        // then
    }

    private MockMvc mockMvc(MemberController controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new AuthExceptionHandler())
                .build();
    }

    private static final class CapturingWithdrawMemberUseCase implements WithdrawMemberUseCase {
        private Long memberId;

        @Override
        public void withdraw(WithdrawMemberCommand command) {
            this.memberId = command.memberId();
        }

        Long memberId() {
            return memberId;
        }
    }
}
