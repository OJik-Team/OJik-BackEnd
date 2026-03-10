package ojik.ojikback.api.adapter.in.member;

import ojik.ojikback.api.adapter.out.common.ApiResponse;
import ojik.ojikback.domain.port.in.member.WithdrawMemberUseCase;
import ojik.ojikback.domain.port.in.member.command.WithdrawMemberCommand;
import ojik.ojikback.infrastructure.adapter.in.security.AuthenticatedMember;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController implements MemberApiSpec {
    private final WithdrawMemberUseCase withdrawMemberUseCase;

    public MemberController(WithdrawMemberUseCase withdrawMemberUseCase) {
        this.withdrawMemberUseCase = withdrawMemberUseCase;
    }

    @Override
    public ApiResponse<Void> withdraw(Authentication authentication) {
        AuthenticatedMember authenticatedMember = (AuthenticatedMember) authentication.getPrincipal();
        withdrawMemberUseCase.withdraw(new WithdrawMemberCommand(authenticatedMember.memberId()));
        return ApiResponse.success(null);
    }
}
