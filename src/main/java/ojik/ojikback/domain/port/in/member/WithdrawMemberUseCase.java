package ojik.ojikback.domain.port.in.member;

import ojik.ojikback.domain.port.in.member.command.WithdrawMemberCommand;

public interface WithdrawMemberUseCase {
    void withdraw(WithdrawMemberCommand command);
}
