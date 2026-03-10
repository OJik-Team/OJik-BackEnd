package ojik.ojikback.domain.service.member;

import java.time.LocalDateTime;
import ojik.ojikback.domain.entity.Member;
import ojik.ojikback.domain.port.in.auth.AuthErrorCode;
import ojik.ojikback.domain.port.in.auth.AuthException;
import ojik.ojikback.domain.port.in.member.WithdrawMemberUseCase;
import ojik.ojikback.domain.port.in.member.command.WithdrawMemberCommand;
import ojik.ojikback.domain.port.out.member.ActiveMemberReader;
import ojik.ojikback.domain.port.out.member.MemberWithdrawalProcessor;

public class WithdrawMemberService implements WithdrawMemberUseCase {
    private final ActiveMemberReader activeMemberReader;
    private final MemberWithdrawalProcessor memberWithdrawalProcessor;

    public WithdrawMemberService(
            ActiveMemberReader activeMemberReader,
            MemberWithdrawalProcessor memberWithdrawalProcessor
    ) {
        this.activeMemberReader = activeMemberReader;
        this.memberWithdrawalProcessor = memberWithdrawalProcessor;
    }

    @Override
    public void withdraw(WithdrawMemberCommand command) {
        Member member = activeMemberReader.findActiveById(command.memberId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND, "존재하지 않는 회원입니다: " + command.memberId()));

        member.withdraw(LocalDateTime.now());
        memberWithdrawalProcessor.withdraw(member);
    }
}
