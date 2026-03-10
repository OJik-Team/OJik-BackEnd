package ojik.ojikback.infrastructure.adapter.in.config;

import ojik.ojikback.domain.port.in.member.WithdrawMemberUseCase;
import ojik.ojikback.domain.port.out.member.ActiveMemberReader;
import ojik.ojikback.domain.port.out.member.MemberWithdrawalProcessor;
import ojik.ojikback.domain.service.member.WithdrawMemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberUseCaseConfig {

    @Bean
    public WithdrawMemberUseCase withdrawMemberUseCase(
            ActiveMemberReader activeMemberReader,
            MemberWithdrawalProcessor memberWithdrawalProcessor
    ) {
        return new WithdrawMemberService(activeMemberReader, memberWithdrawalProcessor);
    }
}
