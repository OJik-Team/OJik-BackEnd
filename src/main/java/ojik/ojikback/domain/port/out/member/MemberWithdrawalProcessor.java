package ojik.ojikback.domain.port.out.member;

import ojik.ojikback.domain.entity.Member;

public interface MemberWithdrawalProcessor {
    void withdraw(Member member);
}
