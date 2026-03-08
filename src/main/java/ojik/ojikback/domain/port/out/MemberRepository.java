package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.Member;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findById(Long memberId);
}
