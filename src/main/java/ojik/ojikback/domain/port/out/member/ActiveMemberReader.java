package ojik.ojikback.domain.port.out.member;

import java.util.Optional;
import ojik.ojikback.domain.entity.Member;

public interface ActiveMemberReader {
    Optional<Member> findActiveById(Long memberId);
}
