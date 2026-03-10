package ojik.ojikback.domain.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Optional;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.entity.Team;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.in.member.command.WithdrawMemberCommand;
import ojik.ojikback.domain.port.out.member.ActiveMemberReader;
import ojik.ojikback.domain.port.out.member.MemberWithdrawalProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WithdrawMemberServiceTest {

    @Test
    @DisplayName("활성 회원이면 soft delete 후 탈퇴 처리를 위임한다")
    void withdrawSuccess() {
        // given
        Member member = Member.restore(
                1L,
                "노을",
                Team.restore(1L, "LG Twins"),
                null,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1),
                null
        );
        StubMemberWithdrawalProcessor processor = new StubMemberWithdrawalProcessor();
        WithdrawMemberService service = new WithdrawMemberService(
                memberId -> Optional.of(member),
                processor
        );

        // when
        service.withdraw(new WithdrawMemberCommand(1L));

        // then
        assertThat(member.getDeletedAt()).isNotNull();
        assertThat(processor.withdrawnMember()).isSameAs(member);
    }

    @Test
    @DisplayName("활성 회원이 아니면 MEMBER_NOT_FOUND 예외를 던진다")
    void withdrawFailWhenMemberMissing() {
        // given
        WithdrawMemberService service = new WithdrawMemberService(
                memberId -> Optional.empty(),
                member -> {
                }
        );

        // when
        assertThatThrownBy(() -> service.withdraw(new WithdrawMemberCommand(1L)))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.MEMBER_NOT_FOUND);

        // then
    }

    private static final class StubMemberWithdrawalProcessor implements MemberWithdrawalProcessor {
        private Member withdrawnMember;

        @Override
        public void withdraw(Member member) {
            this.withdrawnMember = member;
        }

        Member withdrawnMember() {
            return withdrawnMember;
        }
    }
}
